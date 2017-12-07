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
import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.CloseBottomSheetEvent;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.DamageCaseSelected;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.InsuranceCoverageSelected;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.VertexCreated;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.VertexDeleted;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.VertexSelected;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.Helper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.SopraPolygon;

import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.Helper.latLngOf;

/**
 * Binds application specific map logic to GoogleMap instance.
 */
public class SopraMap implements LifecycleObserver {

    @Inject Vibrator vibrator;

    @Inject DamageCaseRepository damageCaseRepository;
    @Inject DamageCaseHandler damageCaseHandler;

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
    private LongSparseArray<PolygonContainer> damagePolygons = new LongSparseArray<>();
    private LongSparseArray<PolygonContainer> insurancePolygons = new LongSparseArray<>();

    SopraMap(GoogleMap googleMap, Context context) {
        SopraApp.getAppComponent().inject(this);

        this.resources = context.getResources();
        this.gMap = googleMap;

        initResources(context);
        initMap();
    }

    private void initMap() {
        /* settings */

        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.setIndoorEnabled(false);

        UiSettings uiSettings = gMap.getUiSettings();
        uiSettings.setTiltGesturesEnabled(false);

        /* bindings */

        gMap.setOnPolygonClickListener(p -> {
            if (isHighlighted) return;

            PolygonContainer polygon = ((PolygonContainer) p.getTag());

            if (polygon.type == PolygonType.DAMAGE_CASE) {
                EventBus.getDefault().post(new DamageCaseSelected(polygon.uniqueId));

            } else {
                EventBus.getDefault().post(new InsuranceCoverageSelected(polygon.uniqueId));
            }
        });

        gMap.setOnCircleClickListener(circle -> {
            if (!isHighlighted) return;

            EventBus.getDefault().post(new VertexSelected((int) circle.getTag()));
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

        // the database tells us what shall exist!
        damageCaseRepository.getAll().observe(damageCaseHandler, listOfDamageCases -> {
            if (listOfDamageCases == null) return;

            clearAllDamages();

            for (DamageCase damageCase : listOfDamageCases) {
                loadPolygonOf(
                        damageCase.getCoordinates(),
                        PolygonType.DAMAGE_CASE,
                        damageCase.getID()
                );
            }
        });
    }

    // LifecycleObserver ###########################################################################

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

    /* <----- event handling methods -----> */

    @Subscribe
    public void onVertexCreated(VertexCreated event) {
        if (activePolygon == null) {
            newPolygon(event.position, PolygonType.DAMAGE_CASE);
        }

        activePolygon.addAndDisplay(event.position);
        activePolygon.redrawHighlightCircles();

        refreshAreaLivedata();
    }

    @Subscribe
    public void onVertexSelected(VertexSelected event) {
        makeDraggable(polygonHighlightVertex.get(event.vertexNumber));
    }

    @Subscribe
    public void onVertexDeleted(VertexDeleted event) {
        activePolygon.removeAndDisplay(event.vertexNumber);
        activePolygon.redrawHighlightCircles();

        refreshAreaLivedata();
    }

    @Subscribe
    public void onDamageCaseSelected(DamageCaseSelected event) {
        polygonFrom(event.uniqueId, PolygonType.DAMAGE_CASE).highlight();

        refreshAreaLivedata();
    }

    @Subscribe
    public void onInsuranceCoverageSelected(InsuranceCoverageSelected event) {
        polygonFrom(event.uniqueId, PolygonType.INSURANCE_COVERAGE).highlight();

        refreshAreaLivedata();
    }

    @Subscribe
    public void onCloseBottomSheet(CloseBottomSheetEvent event) {
        if (activePolygon != null && isHighlighted) {
            activePolygon.highlight();
        }

        reloadDamageCases();
    }

   /* <----- exposed methods -----> */

    List<LatLng> getActivePoints() {
        return activePolygon.data.getPoints();
    }

    LiveData<Double> areaLiveData() {
        return currentArea;
    }

    double getArea() {
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
                        .strokeColor(resources.getColor(R.color.accent, null))
                        .fillColor(resources.getColor(R.color.accent_15percent, null))
                        .zIndex(0);

        userPositionIndicator = gMap.addCircle(options);
        userPositionIndicatorCenter =
                gMap.addCircle(
                        options
                                .radius(1)
                                .strokeWidth(10)
                                .fillColor(resources.getColor(R.color.accent, null))
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
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17)));
    }

    void mapCameraJump(List<LatLng> polygon) {
        // jumping to the location of the polygons centroid
        gMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17)));
    }

    void mapCameraMove(LatLng target) {
        // panning to the location of the target
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17)));
    }

    void mapCameraMove(List<LatLng> polygon) {
        // panning to the location of the polygons centroid
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17)));
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


    private void loadPolygonOf(List<LatLng> coordinates, PolygonType type, long uniqueId) {
        PolygonContainer polygon =
                (PolygonContainer)
                        drawPolygonOf(coordinates, type, uniqueId).getTag();

        polygon.storedIn().put(uniqueId, polygon);
    }

    private void reloadDamageCases() {
        clearAllDamages();

        List<DamageCase> damageCases = damageCaseRepository.getAll().getValue();

        if (damageCases == null) return;

        for (DamageCase damageCase : damageCases) {
            loadPolygonOf(
                    damageCase.getCoordinates(),
                    PolygonType.DAMAGE_CASE,
                    damageCase.getID()
            );
        }
    }

    private void clearAllDamages() {
        PolygonContainer polygon;

        for (int i = 0; i < damagePolygons.size(); ++i) {
            long key = damagePolygons.keyAt(i);

            polygon = damagePolygons.get(key);
            polygon.mapObject.remove();
        }

        damagePolygons.clear();
    }

    private void removeActivePolygon() {
        if (activePolygon == null) return;

        activePolygon.highlight();

        activePolygon.storedIn().delete(activePolygon.uniqueId);
        activePolygon.mapObject.remove();
    }

    private PolygonContainer polygonFrom(long uniqueId, PolygonType type) {

        if (type == PolygonType.DAMAGE_CASE) {
            return damagePolygons.get(uniqueId);

        } else {
            return insurancePolygons.get(uniqueId);
        }
    }

    private CameraPosition cameraPosOf(LatLng target, int zoom) {
        return new CameraPosition.Builder()
                .target(target).zoom(zoom).build();
    }

    private void refreshAreaLivedata() {
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

    private void makeDraggable(Circle circle) {

        int circleIndex = (int) circle.getTag();

        if (circleIndex == indexActiveVertex) {
            removeMarker();
            return;
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
                return insurancePolygons;
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

        void highlight() {
            if (isHighlighted) {
                indexActiveVertex = -1;

                this.removeHighlightCircles();

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
                    options.fillColor(resources.getColor(R.color.accent, null));

                } else if (i == points.size()-2) {
                    options.fillColor(resources.getColor(R.color.accent_light, null));
                }

                Circle circle = gMap.addCircle(options);

                polygonHighlightVertex.add(circle);
                circle.setTag(i);
            }
        }

    }
}
