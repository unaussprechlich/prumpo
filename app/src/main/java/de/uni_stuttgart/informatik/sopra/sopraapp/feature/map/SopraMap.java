package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.arch.persistence.room.TypeConverter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.Helper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.SopraPolygon;

/**
 * Binds application specific map logic to GoogleMap instance.
 */
public class SopraMap {

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
    private PolygonContainer babyPolygon;
    private HashMap<String, PolygonContainer> polygonStorage = new HashMap<>();

    @Inject
    Vibrator vibrator;

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
            PolygonContainer polygon  = polygonStorage.get(p.getTag());

            polygon.highlight();
        });

        gMap.setOnCircleClickListener(circle -> {
            if (!isHighlighted) return;

            makeDraggable(circle);
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

    /* <--- exposed section ---> */

    void createPolygon(LatLng startPoint, PolygonType type, String uniqueId) {
        List<LatLng> points = new ArrayList<>();
        points.add(startPoint);

        drawPolygonOf(points, type, uniqueId);

        highlight(uniqueId);
    }

    void addVertex(LatLng position) {
        List<LatLng> points = activePolygon.data.getPoints();
        points.add(position);

        activePolygon.mapObject.setPoints(points);

        activePolygon.removeHighlightCircles();
        activePolygon.drawHighlightCircles();
    }

    void removeVertex(int vertexNumber) {
        List<LatLng> points = activePolygon.data.getPoints();
        points.remove(vertexNumber);

        activePolygon.mapObject.setPoints(points);

        activePolygon.removeHighlightCircles();
        activePolygon.drawHighlightCircles();
    }

    double getArea() {
        return activePolygon.data.getArea();
    }

    void dragMarkerToggle(int vertexNumber) {
        makeDraggable(polygonHighlightVertex.get(vertexNumber));
    }

    List<LatLng> getActivePoints() {
        return activePolygon.data.getPoints();
    }

    void highlight(String uniqueId) {
        polygonStorage.get(uniqueId).highlight();
    }

    boolean hasActivePolygon() {
        return (activePolygon != null);
    }

    String activePolygonId() {
        return (String) activePolygon.mapObject.getTag();
    }

    void drawPolygonOf(List<LatLng> coordinates, PolygonType type, String uniqueId) {

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
        polygonMapObject.setTag(uniqueId);

        polygonStorage.put(
                uniqueId,
                new PolygonContainer(
                        polygonMapObject,
                        SopraPolygon.loadPolygon(coordinates),
                        PolygonType.DAMAGE_CASE
                )
        );
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
        // jumping to the location of the data
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17)));
    }

    void mapCameraJump(List<LatLng> polygon) {
        // jumping to the location of the polygons centroid
        gMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17)));
    }

    void mapCameraMove(LatLng target) {
        // panning to the location of the data
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17)));
    }

    void mapCameraMove(List<LatLng> polygon) {
        // panning to the location of the polygons centroid
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17)));
    }

    /* <--- helper section ---> */

    @TypeConverter
    private LatLng latLngOf(Location position) {
        if (position == null) {
            return null;
        }

        return new LatLng(position.getLatitude(), position.getLongitude());
    }

    private CameraPosition cameraPosOf(LatLng target, int zoom) {
        return new CameraPosition.Builder()
                .target(target).zoom(zoom).build();
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

        LatLng markerPosition = marker.getPosition();

        activePolygon.data.movePoint(indexActiveVertex, markerPosition);
        List<LatLng> points = activePolygon.data.getPoints();

        activePolygon.mapObject.setPoints(points);

        previewPolyline.remove();
        previewPolyline = null;

        activePolygon.removeHighlightCircles();
        activePolygon.drawHighlightCircles();
    }

    /**
     * Combines SopraPolygon's validation/data logic
     * with polygon objects on the google map
     */
    private class PolygonContainer {

        PolygonType type;

        Polygon mapObject;
        SopraPolygon data;

        PolygonContainer(Polygon polygonMapObject, SopraPolygon polygonData, PolygonType type) {
            this.mapObject = polygonMapObject;
            this.data = polygonData;

            this.type = type;
        }

        PolygonContainer(Polygon polygonVisible, PolygonType type) {
            this(polygonVisible, new SopraPolygon(), type);
        }

        private void highlight() {
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

        private void removeHighlightCircles() {
            for (Circle vertex : polygonHighlightVertex) {
                vertex.remove();
            }

            polygonHighlightVertex.clear();
            removeMarker();
        }

        private void drawHighlightCircles() {

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
