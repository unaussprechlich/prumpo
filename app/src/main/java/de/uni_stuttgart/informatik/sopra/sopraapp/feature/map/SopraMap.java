package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.content.res.Resources;

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

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;

/**
 * Binds application specific map logic to GoogleMap instance.
 */

public class SopraMap {

    private Resources resources;
    private GoogleMap gMap;

    private Polygon visiblePolygon;
    private List<Circle> polygonHighlightVertex = new ArrayList<>();

    private Marker dragMarker;
    private boolean isHighlighted;
    private int indexActiveVertex = -1;

    private SopraPolygon polygonData = new SopraPolygon();

    SopraMap(GoogleMap googleMap, Resources resources) {
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
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                dragMarker(marker);

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

        if (dragMarker == null) {
            MarkerOptions options =
                    new MarkerOptions()
                            .position(circle.getCenter())
                            .draggable(true)
                            .zIndex(2);

            dragMarker = gMap.addMarker(options);
        }

        dragMarker.setPosition(circle.getCenter());
    }

    private void dragMarker(Marker marker) {
        if (indexActiveVertex < 0) return;

        LatLng markerPosition = marker.getPosition();

        polygonData.movePoint(indexActiveVertex, markerPosition);
        List<LatLng> points = polygonData.getPoints();

        visiblePolygon.setPoints(points);

        removeHighlight();
        highlight(points);
    }

    private void highlight(List<LatLng> points) {
        for (int i = 0; i < points.size(); ++i) {
            drawCircle(points.get(i), i);
        }
    }

    private void removeHighlight() {
        for (Circle highlightVertex : polygonHighlightVertex) {
            highlightVertex.remove();
        }

        if (dragMarker != null) {
            dragMarker.remove();
            dragMarker = null;
        }
    }

    void drawCircle(LatLng point, Object tag) {
        Circle circle = gMap.addCircle(
                new CircleOptions()
                        .fillColor(resources.getColor(R.color.contrastComplement, null))
                        .center(point)
                        .strokeWidth(4)
                        .radius(3)
                        .zIndex(1)
                        .clickable(true));

        polygonHighlightVertex.add(circle);

        circle.setTag(tag);
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
