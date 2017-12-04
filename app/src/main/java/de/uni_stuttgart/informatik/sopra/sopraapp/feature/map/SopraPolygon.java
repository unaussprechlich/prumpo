package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Polygon model-class containing application specific logic.
 */
class SopraPolygon {

    List<LatLng> vertices = new ArrayList<>();

    boolean addPoint(LatLng point) {
        vertices.add(point);

        if (notIntersecting()) return true;

        vertices.remove(vertices.size()-1);
        return false;
    }

    List<LatLng> getPoints() {
        return vertices;
    }

    LatLng getPoint(int index) {

        return vertices.get(index);
    }

    boolean movePoint(int index, LatLng target) {

        int lastIndex = vertices.size()-1;
        boolean isFirstOrLast = (index == 0) || (index == lastIndex);

        LatLng oldPoint = vertices.get(index);

        vertices.set(index, target);

        if (isValidPolygon()) return true;

        /* polygon was invalid; reverting changes! */

        vertices.set(index, oldPoint);

        return false;
    }

    int getVertexCount() {
        return vertices.size();
    }

    double getArea() {
        return Helper.areaOfPolygon(vertices);
    }

    LatLng getCentroid() {
        return Helper.centroidOfPolygon(vertices);
    }

    boolean isValidPolygon() {
        return vertices.size() > 2 && notIntersecting();
    }

    static SopraPolygon loadPolygon(List<LatLng> vertices) {

        SopraPolygon polygon = new SopraPolygon();

        if (polygon.setPoints(vertices)) {
            return polygon;
        }

        throw new IllegalArgumentException("" +
                "'vertices' coordinates must form a correct polygon " +
                "(i.e. non self-intersecting and at least triangular)!"
        );
    }

    private boolean setPoints(List<LatLng> points) {
        vertices = points;

        return isValidPolygon();
    }

    // TODO: implement intersection check
    private boolean notIntersecting() {
        return true;
    }
}
