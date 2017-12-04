package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.content.res.Resources;
import android.os.Vibrator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
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
import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.ROOM_WHITE_BITMAP_DESCRIPTOR;

/**
 * Binds application specific map logic to GoogleMap instance.
 */
public class SopraMap {

    private Resources resources;
    private GoogleMap gMap;

    private Polygon visiblePolygon;
    private Polyline previewPolyline;
    private List<Circle> polygonHighlightVertex = new ArrayList<>();

    private Marker dragMarker;
    private boolean isHighlighted;
    private int indexActiveVertex = -1;

    private SopraPolygon polygonData = new SopraPolygon();

    @Inject
    Vibrator vibrator;

    SopraMap(GoogleMap googleMap, Resources resources) {
        SopraApp.getAppComponent().inject(this);
        this.resources = resources;

        this.gMap = googleMap;

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
            if (isHighlighted) {
                isHighlighted = false;
                indexActiveVertex = -1;

                removeHighlight();

                return;
            }

            isHighlighted = true;
            highlight(polygonData.getPoints());
        });

        gMap.setOnCircleClickListener(circle -> {
            if (!isHighlighted) return;

            makeDraggable(circle);
        });

        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                vibrator.vibrate(200);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                onMarkerMove(marker);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                onMarkerDown(marker);
                vibrator.vibrate(100);

                // to fix zooming issue (suddenly setting a navigational tag upon leaving zoom)
                marker.setPosition(polygonData.getPoint(indexActiveVertex));
            }
        });

        // to KILL g-maps native single-click functionality
        gMap.setOnMarkerClickListener(marker -> true);
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
                            .icon(ROOM_WHITE_BITMAP_DESCRIPTOR);

            dragMarker = gMap.addMarker(options);
        }

        dragMarker.setPosition(circle.getCenter());
    }

    private void onMarkerMove(Marker marker) {

        List<LatLng> adjacentPoints = new ArrayList<>();
        int vertexCount = polygonData.getVertexCount();

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

        adjacentPoints.add(polygonData.getPoint(indexLeft));
        adjacentPoints.add(marker.getPosition());
        adjacentPoints.add(polygonData.getPoint(indexRight));

        previewPolyline.setPoints(adjacentPoints);
    }

    private void onMarkerDown(Marker marker) {

        if (indexActiveVertex < 0) return;

        LatLng markerPosition = marker.getPosition();

        polygonData.movePoint(indexActiveVertex, markerPosition);
        List<LatLng> points = polygonData.getPoints();

        visiblePolygon.setPoints(points);

        previewPolyline.remove();
        previewPolyline = null;

        removeHighlight();
        highlight(points);
    }

    private void highlight(List<LatLng> points) {

        for (int i = 0; i < points.size(); ++i) {

            Circle circle = gMap.addCircle(
                    new CircleOptions()
                            .fillColor(resources.getColor(R.color.contrastComplement, null))
                            .center(points.get(i))
                            .strokeWidth(4)
                            .radius(3)
                            .zIndex(1)
                            .clickable(true));

            polygonHighlightVertex.add(circle);

            circle.setTag(i);
        }
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



    void drawPolygonOf(List<LatLng> coordinates) {
        polygonData = SopraPolygon.loadPolygon(coordinates);

        PolygonOptions rectOptions =
                new PolygonOptions()
                        .addAll(coordinates)
                        .geodesic(true)
                        .clickable(true)
                        .strokeJointType(JointType.ROUND)
                        .strokeColor(resources.getColor(R.color.red, null))
                        .fillColor(resources.getColor(R.color.damage_alpha, null));

        visiblePolygon = gMap.addPolygon(rectOptions);
    }

    void mapCameraJump(LatLng target) {
        // jumping to the location of the polygonData
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17)));
    }

    void mapCameraJump(List<LatLng> polygon) {
        // jumping to the location of the polygons centroid
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17)));
    }

    void mapCameraMove(LatLng target) {
        // panning to the location of the polygonData
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 17)));
    }

    void mapCameraMove(List<LatLng> polygon) {
        // panning to the location of the polygons centroid
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(Helper.centroidOfPolygon(polygon), 17)));
    }

}
