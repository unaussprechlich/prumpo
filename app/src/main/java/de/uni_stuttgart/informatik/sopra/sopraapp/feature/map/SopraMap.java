package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.LongSparseArray;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsPolygonSelected;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsVertex;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.Helper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.SopraPolygon;

import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.Helper.latLngOf;

/**
 * Binds application specific map logic to GoogleMap instance.
 */
public class SopraMap implements LifecycleObserver {

    // TODO: pls refactor me, senpai!

    @Inject DamageCaseRepository damageCaseRepository;
    @Inject DamageCaseHandler damageCaseHandler;

    @Inject ContractRepository contractRepository;
    @Inject ContractHandler contractHandler;

    @Inject Vibrator vibrator;

    private MutableLiveData<Double> currentArea = new MutableLiveData<>();

    private static BitmapDescriptor ROOM_ACCENT_BITMAP_DESCRIPTOR; // TODO: extract

    private Resources resources;
    private GoogleMap gMap;

    private List<Circle> polygonHighlightVertex = new ArrayList<>();

    private Circle userPositionIndicator;
    private Circle userPositionIndicatorCenter;
    private Location lastUserLocation;

    private Polyline previewPolyline;
    private Marker dragMarker;

    private boolean isHighlighted;
    private int indexActiveVertex = -1;

    private PolygonContainer activePolygon;
    private LongSparseArray<PolygonContainer> damagePolygons = new LongSparseArray<>();
    private LongSparseArray<PolygonContainer> contractPolygons = new LongSparseArray<>();

    private List<DamageCase> cachedDamageCases;
    private List<Contract> cachedContracts;

    SopraMap(GoogleMap googleMap, Context context, int viewType) {
        SopraApp.getAppComponent().inject(this);

        this.resources = context.getResources();
        this.gMap = googleMap;

        initResources(context);
        initMap(viewType);
    }

    private void initMap(int viewType) {
        /* settings */

        gMap.setMapType(viewType);
        gMap.setIndoorEnabled(false);

        UiSettings uiSettings = gMap.getUiSettings();
        uiSettings.setTiltGesturesEnabled(false);

        /* bindings */

        gMap.setOnPolygonClickListener(p -> {
            if (isHighlighted) return;

            PolygonContainer polygon = ((PolygonContainer) p.getTag());

            if (polygon == null) return;

            if (polygon.type == PolygonType.DAMAGE_CASE) {
                EventBus.getDefault()
                        .post(new EventsPolygonSelected.DamageCase(polygon.uniqueId));

            } else {
                EventBus.getDefault()
                        .post(new EventsPolygonSelected.Contract(polygon.uniqueId));
            }
        });

        gMap.setOnCircleClickListener(circle -> {
            if (!isHighlighted) return;

            EventBus.getDefault().post(new EventsVertex.Selected((int) circle.getTag()));
        });

        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                // initial haptic feedback
                vibrator.vibrate(200);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                SopraMap.this.onMarkerDrag(marker);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                SopraMap.this.onMarkerDragEnd(marker);

                // final haptic feedback
                vibrator.vibrate(100);
            }
        });

        // to KILL g-maps native single-click functionality
        gMap.setOnMarkerClickListener(marker -> true);

    }

    /* <----- lifecycle events -----> */

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    /* <----- EventBus event handling methods -----> */

    @Subscribe(sticky = true)
    public void onLogin(EventsAuthentication.Login event) {
        // the database tells us what shall exist!
        damageCaseRepository.getAll().observe(damageCaseHandler, damageCases -> {
            if (damageCases == null) return;


            if (cachedDamageCases != null) {
                cachedDamageCases.clear();
            }

            cachedDamageCases = new ArrayList<>(damageCases);

            List<PolygonContainer> containersToSynchronize = new ArrayList<>();

            for (DamageCase damageCase : cachedDamageCases) {
                long polygonId = damageCase.getID();

                PolygonContainer damageContainer =
                        new PolygonContainer(
                                polygonId, null,
                                SopraPolygon.loadPolygon(damageCase.getCoordinates()),
                                PolygonType.DAMAGE_CASE
                        );

                containersToSynchronize.add(damageContainer);
            }

            synchronizePolygon(containersToSynchronize);
        });

        damageCaseHandler.getLiveData().observe(damageCaseHandler, damageCase -> {
            if (damageCase == null) return;


            if (activePolygon != null) {
                // avoid deselecting the polygon again, when called twice in a row
                if (damageCase.getID() == activePolygon.uniqueId) return;
            }

            selectPolygon(damageCase.getID(), PolygonType.DAMAGE_CASE);
        });

        /* repeat the same binding-pattern for contracts */

        contractRepository.getAll().observe(contractHandler, contracts -> {
            if (contracts == null) return;

            if (cachedContracts != null) {
                cachedContracts.clear();
            }

            cachedContracts = new ArrayList<>(contracts);

            List<PolygonContainer> containersToSynchronize = new ArrayList<>();

            for (Contract contract : cachedContracts) {
                long polygonId = contract.getID();

                PolygonContainer contractContainer =
                        new PolygonContainer(
                                polygonId, null,
                                SopraPolygon.loadPolygon(contract.getCoordinates()),
                                PolygonType.CONTRACT
                        );

                containersToSynchronize.add(contractContainer);
            }

            synchronizePolygon(containersToSynchronize);
        });

        contractHandler.getLiveData().observe(contractHandler, contract -> {
            if (contract == null) return;

            if (activePolygon != null) {
                if (contract.getID() == activePolygon.uniqueId) return;
            }

            selectPolygon(contract.getID(), PolygonType.CONTRACT);
        });

    }

    @Subscribe
    public void onVertexCreated(EventsVertex.Created event) {
        if (activePolygon == null) {
            newPolygon(event.position, event.polygonType);
            return;
        }

        activePolygon.addAndDisplay(event.position);
        activePolygon.redrawHighlightCircles();

        refreshAreaLivedata();
    }

    @Subscribe
    public void onVertexSelected(EventsVertex.Selected event) {
        makeDraggableAndMove(polygonHighlightVertex.get(event.vertexNumber));
    }

    @Subscribe
    public void onVertexDeleted(EventsVertex.Deleted event) {
        activePolygon.removeAndDisplay(event.vertexNumber);
        activePolygon.redrawHighlightCircles();

        refreshAreaLivedata();
    }

    @Subscribe
    public void onAbortBottomSheet(EventsBottomSheet.ForceClose event) {
        // TODO: fix not removing newly created polygon on force close!
        System.out.println("ABORT");
        removeActivePolygon();
        reloadDamageCases();
    }

    @Subscribe
    public void onCloseBottomSheet(EventsBottomSheet.Close event) {
        System.out.println("CLOSE");
        deselectActivePolygon();
    }

   /* <----- exposed methods -----> */

    public List<LatLng> getActivePoints() {
        return activePolygon.data.getPoints();
    }

    public LiveData<Double> areaLiveData() {
        return currentArea;
    }

    public void updateMapType(int viewType) {
        if (gMap == null) return;

        gMap.setMapType(viewType);
    }

    public double getArea() {
        if (activePolygon == null) return 0;

        return activePolygon.data.getArea();
    }

    boolean hasActivePolygon() {
        return (activePolygon != null);
    }

    long activePolygonId() {
        return activePolygon.uniqueId;
    }

    void drawUserPositionIndicator(Location location) {
        lastUserLocation = location;

        float radius = location.getAccuracy();

        if (radius == 0) {
            radius = 5;
        }

        if (userPositionIndicator != null) {
            userPositionIndicator.remove();
            userPositionIndicatorCenter.remove();
        }

        CircleOptions options =
                new CircleOptions()
                        .center(latLngOf(location))
                        .radius(radius)
                        .strokeWidth(4f)
                        .strokeColor(resources.getColor(R.color.coffee_1_def, null))
                        .fillColor(resources.getColor(R.color.accent_15percent, null))
                        .zIndex(0);

        userPositionIndicator = gMap.addCircle(options);
        userPositionIndicatorCenter =
                gMap.addCircle(
                        options
                                .radius(1)
                                .strokeWidth(10)
                                .fillColor(resources.getColor(R.color.coffee_1_def, null))
                );
    }

    void removeUserPositionIndicator() {
        if (userPositionIndicator != null) {
            userPositionIndicator.remove();
            userPositionIndicatorCenter.remove();
        }

        lastUserLocation = null;
    }

    void mapCameraMoveToUser() {
        if (lastUserLocation == null) return;

        mapCameraMove(latLngOf(lastUserLocation));
    }

    void mapCameraJump(LatLng target) {
        // jumping to the location of the target
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17.5f)));
    }

    void mapCameraJump(List<LatLng> polygon) {
        // jumping to the location of the polygons centroid
        gMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17.5f)));
    }

    void mapCameraMove(LatLng target) {
        // panning to the location of the target
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17.5f)));
    }

    void mapCameraMove(List<LatLng> polygon) {
        // panning to the location of the polygons centroid
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17.5f)));
    }

    /* <----- helper section -----> */

    /**
     * Must only be called, when a *new* polygon with a single starting point
     * is to be created;
     *
     * @param startPoint    the initial point on the Map
     * @param type          determines color (and implies behaviour according to {@link PolygonType})
     */
    private void newPolygon(LatLng startPoint, PolygonType type) {

        SopraPolygon sopraPolygon = new SopraPolygon();
        sopraPolygon.addPoint(startPoint);

        activePolygon =
                new PolygonContainer(
                        -1,
                        drawPolygonOf(sopraPolygon.getPoints(), type, -1),
                        sopraPolygon,
                        type
                );

        activePolygon.toggleHighlight();
    }

    private Polygon drawPolygonOf(List<LatLng> coordinates, PolygonType type, long uniqueId) {

        int strokeColor;
        int fillColor;
        float strokeWidth = 10;

        if (type == PolygonType.DAMAGE_CASE) {
            strokeColor = resources.getColor(R.color.orange, null);
            fillColor = resources.getColor(R.color.orange_50percent, null);

        } else {
            strokeColor = resources.getColor(R.color.white, null);
            fillColor = resources.getColor(R.color.white_15percent, null);
            strokeWidth = 18;
        }

        PolygonOptions rectOptions =
                new PolygonOptions()
                        .addAll(coordinates)
                        .geodesic(true)
                        .clickable(true)
                        .strokeJointType(JointType.ROUND)
                        .strokeColor(strokeColor)
                        .strokeWidth(strokeWidth)
                        .fillColor(fillColor);

        Polygon polygonMapObject = gMap.addPolygon(rectOptions);

        PolygonContainer polygon =
                new PolygonContainer(
                        uniqueId,
                        polygonMapObject,
                        SopraPolygon.loadPolygon(coordinates),
                        type
                );

        polygonMapObject.setTag(polygon);

        return polygonMapObject;
    }

    private void selectPolygon(long uniqueId, PolygonType type) {
        PolygonContainer polygon = polygonFrom(uniqueId, type);
        if (polygon == null) return;

        polygon.toggleHighlight();

        mapCameraMove(polygon.data.getCentroid());

        refreshAreaLivedata();
    }

    void loadPolygonOf(List<LatLng> coordinates, PolygonType type, long uniqueId) {
        PolygonContainer polygon =
                (PolygonContainer)
                        drawPolygonOf(coordinates, type, uniqueId)
                                .getTag();

        polygon.storedIn().put(uniqueId, polygon);
    }

    private void reloadDamageCases() {
        if (cachedDamageCases == null) {
            return;
        }

        clearAllDamages();

        for (DamageCase damageCase : cachedDamageCases) {
            loadPolygonOf(
                    damageCase.getCoordinates(),
                    PolygonType.DAMAGE_CASE,
                    damageCase.getID()
            );
        }
    }

    private void clearAllDamages() {

        removeActivePolygon();

        PolygonContainer polygon;

        for (int i = 0; i < damagePolygons.size(); ++i) {
            long key = damagePolygons.keyAt(i);

            polygon = damagePolygons.get(key);
            polygon.removeMapObject();
        }

        damagePolygons.clear();
    }

    private void deselectActivePolygon() {
        if (activePolygon == null || !isHighlighted) return;

        activePolygon.toggleHighlight();
    }

    private void removeActivePolygon() {
        if (activePolygon == null || !isHighlighted) return;

        // -1 == temporary polygon, which isn't stored yet anyways, so no need to delete it
        if (activePolygon.uniqueId != -1) {
            activePolygon.storedIn().delete(activePolygon.uniqueId);
        }

        activePolygon.removeMapObject();
        activePolygon.toggleHighlight();
        activePolygon = null;
    }

    private PolygonContainer polygonFrom(long uniqueId, PolygonType type) {

        if (type == PolygonType.DAMAGE_CASE) {
            return damagePolygons.get(uniqueId);

        } else {
            return contractPolygons.get(uniqueId);
        }
    }

    private CameraPosition cameraPosOf(LatLng target, float zoom) {
        return new CameraPosition.Builder()
                .target(target).zoom(zoom).build();
    }

    private void refreshAreaLivedata() {
        if (activePolygon == null) return;

        currentArea.postValue(activePolygon.data.getArea());
    }

    private void initResources(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_room_accent);
        Bitmap bitmap =
                Bitmap.createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888
                );
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        ROOM_ACCENT_BITMAP_DESCRIPTOR = BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private boolean makeDraggable(Circle circle) {

        int circleIndex = (int) circle.getTag();

        if (circleIndex == indexActiveVertex) {
            removeMarker();
            return false;
        }

        indexActiveVertex = circleIndex;

        LatLng circlePosition = circle.getCenter();

        if (previewPolyline == null) {
            PolylineOptions lineOptions =
                    new PolylineOptions()
                            .width(3)
                            .color(resources.getColor(R.color.contrastComplement, null));

            previewPolyline = gMap.addPolyline(lineOptions);
        }

        if (dragMarker == null) {
            MarkerOptions options =
                    new MarkerOptions()
                            .position(circlePosition)
                            .draggable(true)
                            .zIndex(2)
                            .anchor(0.5f, 0.98f)
                            .icon(ROOM_ACCENT_BITMAP_DESCRIPTOR);

            dragMarker = gMap.addMarker(options);
        }

        dragMarker.setPosition(circle.getCenter());
        return true;
    }

    private void makeDraggableAndMove(Circle circle) {
        boolean wasSet = makeDraggable(circle);

        if (wasSet) {
            mapCameraMove(circle.getCenter());
        }
    }

    private void removeMarker() {
        if (dragMarker == null) return;

        dragMarker.remove();
        dragMarker = null;

        indexActiveVertex = -1;
    }

    private void onMarkerDrag(Marker marker) {

        List<LatLng> adjacentPoints = new ArrayList<>();
        int vertexCount = activePolygon.data.getVertexCount();

        int indexLeft = indexActiveVertex - 1;
        int indexRight = indexActiveVertex + 1;

        /* wrap around */

        if (indexLeft == -1) {
            indexLeft = vertexCount-1;
        }

        if (indexRight == vertexCount) {
            indexRight = 0;
        }

        /* actual preview */

        adjacentPoints.add(activePolygon.data.getPoint(indexLeft));
        adjacentPoints.add(marker.getPosition());
        adjacentPoints.add(activePolygon.data.getPoint(indexRight));

        previewPolyline.setPoints(adjacentPoints);
    }

    private void onMarkerDragEnd(Marker marker) {

        // TODO: this should be an assertion
        if (indexActiveVertex < 0) return;

        activePolygon.moveAndDisplay(indexActiveVertex, marker.getPosition());

        previewPolyline.remove();
        previewPolyline = null;

        int tmpIndex = indexActiveVertex;
        activePolygon.redrawHighlightCircles();
        makeDraggable(polygonHighlightVertex.get(tmpIndex));

        refreshAreaLivedata();
    }

    private void synchronizePolygon(List<PolygonContainer> polygonContainers) {
        if(polygonContainers.size() == 0) return; //TODO Hotfix
        Set<Long> caseIds = new HashSet<>();

        PolygonContainer polygon;

        // determine which of the two LongSparseArrays we'll want to access
        LongSparseArray<PolygonContainer> polygons = polygonContainers.get(0).storedIn();

        for (PolygonContainer polygonContainer : polygonContainers) {

            long polygonID = polygonContainer.uniqueId;
            PolygonType polygonType = polygonContainer.type;
            List<LatLng> coordinates = polygonContainer.data.getPoints();

            caseIds.add(polygonID);

            polygon = polygons.get(polygonID);

            // polygon not displayed on the map yet, so that must change!
            if (polygon == null) {
                loadPolygonOf(
                        coordinates,
                        polygonType,
                        polygonID
                );

                continue;
            }

            // assert polygonId == polygon.uniqueId

            /* polygon exists on the map, we update points */
            polygon.removeMapObject();
            polygon.mapObject =
                    drawPolygonOf(
                            coordinates,
                            polygonType,
                            polygonID
                    );

            // and redraw highlights in case it was currently selected
            if (polygon == activePolygon) {
                activePolygon.redrawHighlightCircles();
            }
        }

        // ultimately, remove all remaining map-objects that weren't in the DB
        for (int i = 0; i < polygons.size(); ++i) {
            long key = polygons.keyAt(i);
            polygon = polygons.get(key);

            if (!caseIds.contains(polygon.uniqueId)) {
                polygon.removeMapObject();
                polygons.remove(key);

                if (polygon == activePolygon) {
                    polygon.toggleHighlight();
                }
            }
        }
    }

    /**
     * Combines SopraPolygon's validation/data logic
     * with polygon objects on the google map
     */
    private class PolygonContainer {

        long uniqueId;
        PolygonType type;

        Polygon mapObject;
        SopraPolygon data;

        PolygonContainer(long uniqueId, Polygon polygonMapObject, SopraPolygon polygonData, PolygonType type) {
            this.uniqueId = uniqueId;

            this.mapObject = polygonMapObject;
            this.data = polygonData;

            this.type = type;
        }

        LongSparseArray<PolygonContainer> storedIn() {
            if (type == PolygonType.DAMAGE_CASE) {
                return damagePolygons;

            } else {
                return contractPolygons;
            }
        }

        boolean addAndDisplay(LatLng point) {
             if (!data.addPoint(point)) return false;

            mapObject.setPoints(data.getPoints());
            return true;
        }

        boolean moveAndDisplay(int index, LatLng point) {
            if (!data.movePoint(index, point)) return false;

            mapObject.setPoints(data.getPoints());
            return true;
        }

        boolean removeAndDisplay(int index) {
            if (!data.removePoint(index)) return false;

            mapObject.setPoints(data.getPoints());
            return true;
        }

        void removeMapObject() {
            if (mapObject == null) return;
            mapObject.remove();
        }

        void toggleHighlight() {
            if (isHighlighted) {
                indexActiveVertex = -1;

                activePolygon.removeHighlightCircles();

                // deselect (and unhighlight) polygon if clicked twice in a row
                if (this == activePolygon) {
                    activePolygon = null;
                    isHighlighted = false;
                    return;
                }
            }

            activePolygon = this;
            isHighlighted = true;
            this.drawHighlightCircles();
        }

        void redrawHighlightCircles() {
            removeHighlightCircles();
            drawHighlightCircles();
        }

        void removeHighlightCircles() {
            for (Circle vertex : polygonHighlightVertex) {
                vertex.remove();
            }

            polygonHighlightVertex.clear();

            // TODO: separate from this method and check for side effects
            removeMarker();
        }

        void drawHighlightCircles() {

            List<LatLng> points = data.getPoints();
            CircleOptions options =
                    new CircleOptions()
                            .fillColor(resources.getColor(R.color.contrastComplement, null))
                            .strokeWidth(4)
                            .radius(3)
                            .zIndex(1)
                            .clickable(true);

            for (int i = 0; i < points.size(); ++i) {
                options.center(points.get(i));

                if (i == points.size()-1) {
                    options.fillColor(resources.getColor(R.color.coffee_1_def, null));

                } else if (i == points.size()-2) {
                    options.fillColor(resources.getColor(R.color.coffee_1_lighter, null));
                }

                Circle circle = gMap.addCircle(options);

                polygonHighlightVertex.add(circle);
                circle.setTag(i);
            }
        }

    }
}
