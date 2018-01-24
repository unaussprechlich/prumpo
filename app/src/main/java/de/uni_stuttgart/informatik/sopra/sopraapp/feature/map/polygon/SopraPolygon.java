package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.Helper.contains;

/**
 * Polygon model-class containing application specific logic.
 */
public class SopraPolygon {

    private List<LatLng> vertices = new ArrayList<>();

    public boolean containsPoint(LatLng point) {
        return contains(vertices, point);
    }

    public boolean addPoint(LatLng point) {
        vertices.add(point);

        if (notIntersecting()) return true;

        vertices.remove(vertices.size()-1);
        return false;
    }

    public List<LatLng> getPoints() {
        return vertices;
    }

    public LatLng getPoint(int index) {
        return vertices.get(index);
    }

    public int size() {
        return vertices.size();
    }

    public boolean movePoint(int index, LatLng target, boolean isValidMove) {
        LatLng oldPoint = vertices.get(index);
        vertices.set(index, target);

        if (isValidPolygon() && isValidMove) return true;

        /* polygon was invalid; reverting changes! */

        vertices.set(index, oldPoint);
        return false;
    }

    public boolean removePoint(int index) {
        LatLng oldPoint = vertices.get(index);
        vertices.remove(index);

        if (isValidPolygon()) return true;

        /* polygon was invalid; reverting changes! */

        vertices.add(index, oldPoint);
        return false;
    }

    public int getVertexCount() {
        return vertices.size();
    }


    public double getArea() {
        return Helper.areaOfPolygon(vertices);
    }

    public LatLng getCentroid() {
        return Helper.centroidOfPolygon(vertices);
    }

    public boolean isValidPolygon() {
        return notIntersecting();
    }

    public static SopraPolygon loadPolygon(List<LatLng> vertices) {

        SopraPolygon polygon = new SopraPolygon();

        if (polygon.setPoints(vertices)) {
            return polygon;
        }

        throw new IllegalArgumentException("" +
                "'vertices' coordinates must form a correct polygon " +
                "(i.e. non self-intersecting)!"
        );
    }

    private boolean setPoints(List<LatLng> points) {
        vertices = new ArrayList<>(points);

        return isValidPolygon();
    }

    // TODO: implement intersection check
    private boolean notIntersecting() {
        return true;
//        return !Helper.doesPolygonSelfIntersect(vertices);
    }

}
