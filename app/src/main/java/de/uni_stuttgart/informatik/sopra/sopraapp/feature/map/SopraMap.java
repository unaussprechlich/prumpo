package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.support.graphics.drawable.VectorDrawableCompat;

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

import static android.support.v7.content.res.AppCompatResources.getDrawable;

/**
 * Binds application specific map logic to GoogleMap instance.
 */
public class SopraMap {

    private static BitmapDescriptor ROOM_ACCENT_BITMAP_DESCRIPTOR;

    private Resources resources;
    private GoogleMap gMap;

    private List<Circle> polygonHighlightVertex = new ArrayList<>();

    private Polyline previewPolyline;

    private Marker dragMarker;
    private boolean isHighlighted;
    private int indexActiveVertex = -1;

    // TODO: rework to hold and view List of polygons
    private PolygonContainer activePolygon;
    private HashMap<String, PolygonContainer> polygonStorage = new HashMap<>();

    @Inject
    Vibrator vibrator;

    SopraMap(GoogleMap googleMap, Context context) {
        SopraApp.getAppComponent().inject(this);

        this.resources = context.getResources();
        this.gMap = googleMap;

        VectorDrawableCompat vectorDrawable = (VectorDrawableCompat) getDrawable(context, R.drawable.ic_room_accent);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        ROOM_ACCENT_BITMAP_DESCRIPTOR = BitmapDescriptorFactory.fromBitmap(bitmap);

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

            if (isHighlighted) {
                indexActiveVertex = -1;

                polygon.removeHighlight();

                if (polygon == activePolygon) {
                    activePolygon = null;
                    return;
                }
            }

            activePolygon = polygon;

            isHighlighted = true;
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

                // to fix zooming issue (suddenly setting a navigational tag upon leaving zoom)
                marker.setPosition(activePolygon.data.getPoint(indexActiveVertex));
            }
        });

        // to KILL g-maps native single-click functionality
        gMap.setOnMarkerClickListener(marker -> true);
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
            fillColor = resources.getColor(R.color.white_38percent, null);
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

    void mapCameraJump(LatLng target) {
        // jumping to the location of the data
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17)));
    }

    void mapCameraJump(List<LatLng> polygon) {
        // jumping to the location of the polygons centroid
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17)));
    }

    void mapCameraMove(LatLng target) {
        // panning to the location of the data
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17)));
    }

    void mapCameraMove(List<LatLng> polygon) {
        // panning to the location of the polygons centroid
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17)));
    }

    private CameraPosition cameraPosOf(LatLng target, int zoom) {
        return new CameraPosition.Builder()
                .target(target).zoom(zoom).build();
    }

    private void makeDraggable(Circle circle) {
        indexActiveVertex = ((int) circle.getTag());

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

        if (indexActiveVertex < 0) return;

        LatLng markerPosition = marker.getPosition();

        activePolygon.data.movePoint(indexActiveVertex, markerPosition);
        List<LatLng> points = activePolygon.data.getPoints();

        activePolygon.mapObject.setPoints(points);

        previewPolyline.remove();
        previewPolyline = null;

        activePolygon.removeHighlight();
        activePolygon.highlight();
    }

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

        private void removeHighlight() {
            for (Circle vertex : polygonHighlightVertex) {
                vertex.remove();
            }

            polygonHighlightVertex.clear();

            if (dragMarker != null) {
                dragMarker.remove();
                dragMarker = null;
            }
        }

        private void highlight() {

            List<LatLng> points = data.getPoints();
            CircleOptions options =
                    new CircleOptions()
                            .fillColor(resources.getColor(R.color.contrastComplement, null))
                            .strokeWidth(4)
                            .radius(3)
                            .zIndex(1)
                            .clickable(true);

            for (int i = 0; i < points.size(); ++i) {
                Circle circle = gMap.addCircle(options.center(points.get(i)));

                polygonHighlightVertex.add(circle);
                circle.setTag(i);
            }

        }
    }
}
