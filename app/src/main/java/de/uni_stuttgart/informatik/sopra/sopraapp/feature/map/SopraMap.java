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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsPolygonSelected;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsVertex;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.exceptions.LocationNotFound;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.Helper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.SopraPolygon;

import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.Helper.GERMANY_ROUGH_CENTROID;
import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.Helper.latLngOf;

/**
 * Binds application specific map logic to GoogleMap instance.
 */
public class SopraMap implements LifecycleObserver {

    @Inject DamageCaseRepository damageCaseRepository;
    @Inject DamageCaseHandler damageCaseHandler;

    @Inject ContractRepository contractRepository;
    @Inject ContractHandler contractHandler;

    @Inject Vibrator vibrator;

    private MutableLiveData<Double> currentArea = new MutableLiveData<>();

    private static BitmapDescriptor ROOM_ACCENT_BITMAP_DESCRIPTOR;

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

    private LongSparseArray<PolygonContainer> damageCases = new LongSparseArray<>();
    private LongSparseArray<PolygonContainer> contracts = new LongSparseArray<>();

    private List<PolygonContainer> cacheDamageCase;
    private List<PolygonContainer> cacheContracts;

    private Geocoder geocoder;

    SopraMap(GoogleMap googleMap, Context context, int viewType) {
        SopraApp.getAppComponent().inject(this);
        this.resources = context.getResources();
        this.gMap = googleMap;

        geocoder = new Geocoder(context);

        initResources(context);
        initMap(viewType);

    }

    private void initMap(int viewType) {
        /* settings */

        gMap.setMapType(viewType);
        gMap.setIndoorEnabled(false);

        // ball-park centroid of germany
        mapCameraJump(GERMANY_ROUGH_CENTROID, 5.5f);

        UiSettings uiSettings = gMap.getUiSettings();
        uiSettings.setTiltGesturesEnabled(false);

        /* bindings */

        gMap.setOnPolygonClickListener(p -> {
            if (isHighlighted) return;

            PolygonContainer polygon = ((PolygonContainer) p.getTag());

            if (polygon == null) return;

            if (polygon.type == PolygonType.DAMAGE_CASE) {
                EventBus.getDefault()
                        .postSticky(new EventsPolygonSelected.DamageCase(polygon.uniqueId));

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

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (lastUserLocation == null) return;

            mapCameraJump(latLngOf(lastUserLocation));
        }, 2000);

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
        damageCaseRepository.getAll().observeForever(damageCases -> {

            if (damageCases == null) return;

            List<PolygonContainer> polygons =  damageCases.stream()
                                                .map(this::wrap)
                                                .collect(Collectors.toList());

            reloadPolygons(polygons, PolygonType.DAMAGE_CASE);

            cacheDamageCase = polygons;
        });

        contractRepository.getAll().observeForever(contracts -> {

            if (contracts == null) return;

            List<PolygonContainer> polygons = contracts.stream()
                                                .map(this::wrap)
                                                .collect(Collectors.toList());

            reloadPolygons(polygons, PolygonType.CONTRACT);

            cacheContracts = polygons;
        });

        damageCaseHandler.getLiveData().observeForever(damageCase -> {
            if (damageCase == null) {
                if (activePolygon != null) {
                    if (activePolygon.uniqueId == -1) {
                        removeActivePolygon();

                    } else {
                        deselectActivePolygon();
                    }
                }

                return;
            }

            if (activePolygon != null) {
                if (activePolygon.type == PolygonType.CONTRACT) return;
                // avoid deselecting the polygon again, when called twice in a row
                if (damageCase.getID() == activePolygon.uniqueId) return;
            }

            selectPolygon(damageCase.getID(), PolygonType.DAMAGE_CASE);
        });

        contractHandler.getLiveData().observeForever(contract -> {
            if (contract == null) {
                if (activePolygon != null) {
                    if (activePolygon.uniqueId == -1) {
                        removeActivePolygon();

                    } else {
                        deselectActivePolygon();
                    }
                }

                return;
            }

            if (activePolygon != null) {
                if (activePolygon.type == PolygonType.DAMAGE_CASE) return;
                if (contract.getID() == activePolygon.uniqueId) return;
            }

            selectPolygon(contract.getID(), PolygonType.CONTRACT);
        });

        /* repeat the same binding-pattern for contracts */

    }

    @Subscribe
    public void onVertexCreated(EventsVertex.Created event) {

        if (activePolygon == null) {
            newPolygon(event.position, event.polygonType);
            return;
        }

        if (activePolygon.type == PolygonType.CONTRACT
                && event.polygonType == PolygonType.DAMAGE_CASE) {
            activePolygon.toggleHighlight();
            newPolygon(event.position, PolygonType.DAMAGE_CASE);
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
    public void onBsForceClose(EventsBottomSheet.ForceClose event) {
        removeActivePolygon();
        reloadPolygons(cacheContracts, PolygonType.CONTRACT);
        reloadPolygons(cacheDamageCase, PolygonType.DAMAGE_CASE);
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

    public int getMapType() {
        return gMap.getMapType();
    }

    public double getArea() {
        if (activePolygon == null) return 0;

        return activePolygon.data.getArea();
    }

    public String getAddress() throws LocationNotFound {
        String message = "No valid address was found!";

        if (lastUserLocation == null) throw new LocationNotFound(message);

        List<Address> addresses;

        try {
            addresses =
                    geocoder.getFromLocation(
                            lastUserLocation.getLatitude(),
                            lastUserLocation.getLongitude(),
                            1
                    );

            if (addresses == null) throw new IOException();
            if (addresses.size() == 0) throw new IOException();

        } catch (IOException e) {
            throw new LocationNotFound(message);
        }

        Address address = addresses.get(0);

        String postalCode = address.getPostalCode();
        String locality = address.getLocality();

        if (postalCode == null || locality == null) {
            throw new LocationNotFound(message);
        }

        return String.format("%s %s", address.getPostalCode(), address.getLocality());
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
                        .strokeColor(resources.getColor(R.color.h_purple_lighter, null))
                        .fillColor(resources.getColor(R.color.purple_lighter_30, null))
                        .zIndex(0);

        userPositionIndicator = gMap.addCircle(options);
        userPositionIndicatorCenter =
                gMap.addCircle(
                        options
                                .radius(1)
                                .strokeWidth(10)
                                .fillColor(resources.getColor(R.color.h_purple_lighter, null))
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

    void mapCameraJump(LatLng target, float zoom) {
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, zoom)));
    }

    void mapCameraJump(LatLng target) {
        // jumping to the location of the target
        mapCameraJump(target, 17.5f);
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

    private PolygonContainer wrap(Contract contract) {

        long polygonId = contract.getID();

        return new PolygonContainer(
                polygonId, null,
                SopraPolygon.loadPolygon(contract.getCoordinates()),
                PolygonType.CONTRACT
        );
    }

    private PolygonContainer wrap(DamageCase damageCase) {

        long polygonId = damageCase.getID();

        return new PolygonContainer(
                polygonId, null,
                SopraPolygon.loadPolygon(damageCase.getCoordinates()),
                PolygonType.DAMAGE_CASE
        );
    }

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
        float zIndex;
        float strokeWidth = 10;

        if (type == PolygonType.DAMAGE_CASE) {
            strokeColor = resources.getColor(R.color.map_damagecase_stroke, null);
            fillColor = resources.getColor(R.color.map_damagecase_fill, null);
            zIndex = 2;

        } else {
            strokeColor = resources.getColor(R.color.map_contract_stroke, null);
            fillColor = resources.getColor(R.color.map_contract_fill, null);
            strokeWidth = 18;
            zIndex = 1;
        }

        PolygonOptions rectOptions =
                new PolygonOptions()
                        .addAll(coordinates)
                        .geodesic(false)
                        .clickable(true)
                        .strokeJointType(JointType.ROUND)
                        .strokeColor(strokeColor)
                        .strokeWidth(strokeWidth)
                        .fillColor(fillColor)
                        .zIndex(zIndex);

        Polygon polygonMapObject = gMap.addPolygon(rectOptions);

        PolygonContainer polygon =
                new PolygonContainer(
                        uniqueId,
                        polygonMapObject,
                        SopraPolygon.loadPolygon(coordinates),
                        type
                );
        polygon.printPoints();
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
        if (coordinates.size() == 0) {
            return;
        }
        isStoredIn(type).put(uniqueId,
                (PolygonContainer) drawPolygonOf(coordinates, type, uniqueId).getTag());
    }

    private void reloadPolygons(List<PolygonContainer> polygons, PolygonType type) {
        if (polygons == null) {
            return;
        }

        clearCache(type);
        PolygonContainer polygon;

        for (int i = 0; i < polygons.size(); ++i) {
            polygon = polygons.get(i);

            loadPolygonOf(
                    polygon.data.getPoints(),
                    type,
                    polygon.uniqueId
            );
        }
    }

    private void clearCache(PolygonType type) {
        LongSparseArray<PolygonContainer> cache = isStoredIn(type);

        if (cache == null) return;
        if (cache.size() == 0) return;

        PolygonContainer polygon;

        long uidActive = -2;

        if (activePolygon != null) {
            // TODO check if this makes problems
            if (activePolygon.type == type) {
                uidActive = activePolygon.uniqueId;
            }
        }

        for (int i = 0; i < cache.size(); ++i) {

            long key = cache.keyAt(i);

            if (key == uidActive) continue;

            polygon = cache.get(key);

            if (polygon != null) {
                cache.get(key).removeMapObject();
            }
        }

        cache.clear();
    }

    private void deselectActivePolygon() {
        if (activePolygon == null || !isHighlighted) return;

        activePolygon.toggleHighlight();
    }

    private void removeActivePolygon() {
        System.out.println("REMOVE ACTIVE PRE");
        if (activePolygon == null || !isHighlighted) return;
        System.out.println("REMOVE ACTIVE POST");
        // -1 == temporary polygon, which isn't stored yet anyways, so no need to delete it
        if (activePolygon.uniqueId != -1) {
            isStoredIn(activePolygon.type).remove(activePolygon.uniqueId);
        }

        activePolygon.removeMapObject();
        activePolygon.toggleHighlight();
    }

    private PolygonContainer polygonFrom(long uniqueId, PolygonType type) {
        return isStoredIn(type).get(uniqueId, null);
    }

    private LongSparseArray<PolygonContainer> isStoredIn(PolygonType type) {
        return (type == PolygonType.DAMAGE_CASE)
                        ? damageCases
                        : contracts;
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
                            .color(resources.getColor(R.color.white, null))
                    .zIndex(5);

            previewPolyline = gMap.addPolyline(lineOptions);
        }

        if (dragMarker == null) {
            MarkerOptions options =
                    new MarkerOptions()
                            .position(circlePosition)
                            .draggable(true)
                            .zIndex(4)
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

        if (!activePolygon.moveAndDisplay(indexActiveVertex, marker.getPosition())) {
            vibrator.vibrate(300);
        }

        previewPolyline.remove();
        previewPolyline = null;

        int tmpIndex = indexActiveVertex;
        activePolygon.redrawHighlightCircles();
        makeDraggable(polygonHighlightVertex.get(tmpIndex));

        refreshAreaLivedata();
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

        PolygonContainer(long uniqueId,
                         Polygon polygonMapObject,
                         SopraPolygon polygonData,
                         PolygonType type) {

            this.uniqueId = uniqueId;

            this.mapObject = polygonMapObject;
            this.data = polygonData;

            this.type = type;
        }

        void printPoints() {
            for (int i = 0; i < data.getPoints().size(); ++i) {
                System.out.println(String.format(Locale.getDefault(), "Now Printing Lat Nr [%d]: %f", i, data.getPoint(i).latitude));
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

                // deselect (and unhighlight) polygon if called twice in a row
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
                            .fillColor(resources.getColor(R.color.coffee_1_def, null))
                            .strokeWidth(4)
                            .radius(3)
                            .zIndex(3)
                            .clickable(true);

            for (int i = 0; i < points.size(); ++i) {
                options.center(points.get(i));

                if (i == points.size()-1) {
                    options.fillColor(resources.getColor(R.color.coffee_1_light, null));

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
