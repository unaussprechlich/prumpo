package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides various helper methods for map interactions
 */
public class Helper {

    public static final double EPSILON = 0.00001;

    private static boolean equals(final double a, final double b) {

        if (a == b) return true;

        return Math.abs(a-b) < EPSILON;
    }

    private static int compare(final double a, final double b) {
        return equals(a, b)
                    ? 0
                    : (a < b)
                        ? -1
                        : + 1;
    }

    public static boolean doesPolygonSelfIntersect(List<LatLng> points) {
        if (points.size() == 3) return false;

        ArrayList<Point2D> point2DS = projectAndNormalize(points);

        for (int i = 0; i < point2DS.size(); ++i) {
            for (int j = i+2; j < point2DS.size()+i-1; ++j) {

                Point2D a = circularGet(i, point2DS);
                Point2D b = circularGet(i+1, point2DS);

                Point2D c = circularGet(j, point2DS);
                Point2D d = circularGet(j+1, point2DS);

                if (intersect(a, b, c, d)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean intersect(Point2D a, Point2D b, Point2D c, Point2D d) {
//
//        int orientABC = isCCw(a, b, c);
//        int orientABD = isCCw(a, b, d);
//        int orientCDA = isCCw(c, d, a);
//        int orientCDB = isCCw(c, d, b);
//
//        // the general case;
//        if (orientABC != orientABD && orientCDA != orientCDB)
//            return true;
//
//         /* special cases */
//
////        if (orientABC == 0 && onSegment(a, c, b)) return true;
////
////        if (orientABD == 0 && onSegment(a, d, b)) return true;
////
////        if (orientCDA == 0 && onSegment(c, a, d)) return true;
////
////        if (orientCDB == 0 && onSegment(c, b, d)) return true;
//
//        // neither of those cases hold, return false!
//        return false;

        return isCCw(a, c, d) != isCCw(b, c, d)
                && isCCw(a, b, c) != isCCw(a, b, d);

    }

    private static boolean isCCw(Point2D a, Point2D b, Point2D c) {

//        double val = ( (b.y-a.y) * (c.x-b.x)
//                      -(b.x-a.x) * (c.y-b.y));
//
//        // 0 ==> co-linear
//        if (equals(val, 0)) return 0;
//
//        return (compare(val, 0) > 0)
//                ? 1  // clockwise or
//                : 2; // counter-clockwise otherwise

        return compare((c.y - a.y) * (b.x - a.x), (b.y - a.y) * (c.x - a.x)) > 1;

    }

    private static boolean onSegment(Point2D a, Point2D b, Point2D c) {
        return b.x <= Math.max(a.x, c.x) && b.x >= Math.min(a.x, c.x)
                && b.y <= Math.max(a.y, c.y) && b.y >= Math.min(a.y, c.y);
    }

    // the radius of the earth is approximated in Kilometres (6371 km)
    private static final double RADIUS_EARTH_180TH_PI = Math.PI * 6371/180;

    /**
     * Estimate the area under a polygonal section of the earth, given it's LatLng vertices.
     *
     * @param vertices      LatLng coordinates of the vertices as <src>List</src>(, in any order!)
     *
     * @return              approximated area in hectares
     */
    static double areaOfPolygon(List<LatLng> vertices) {

        if (vertices.size() < 3) return 0;

        double areaSum = 0;

        ArrayList<Point2D> coordinates = projectAndNormalize(vertices);

        for (int i = 0; i < coordinates.size(); ++i) {
            double y = circularGet(i, coordinates).y;
            double lastX = circularGet(i-1, coordinates).x;
            double beforeLastY = circularGet(i-2, coordinates).y;

            areaSum += lastX * (y - beforeLastY);
        }

        return Math.abs(areaSum/2) * 100;
    }

    public static LatLng centroidOfPolygon(List<LatLng> vertices) {
        double length = vertices.size();

        double sumLat = 0;
        double sumLng = 0;

        for (LatLng vertex : vertices) {
            sumLat += vertex.latitude;
            sumLng += vertex.longitude;
        }

        return new LatLng(sumLat/length, sumLng/length);
    }

    /**
     * Projecting and normalizing lat/lng List-objects onto the cartesian plane.
     * <\p>Uses sinusoidal (equal-area) map projection.
     *
     * @param vertices      LatLng coordinates of the vertices as <src>List</src>
     *
     * @return              the projected and normalized output coordinates as Point2D objects
     *                      in order of their angle size, relative to the geographic mean.
     */
    private static ArrayList<Point2D> projectAndNormalize(List<LatLng> vertices) {
        double size = vertices.size();

        Point2D[] coordinates = new Point2D[vertices.size()];

        double longitudeSum = 0;
        double latitudeSum = 0;
        double longitudeAvg;
        double latitudeAvg;

        double xAvg;
        double yAvg;

        /* pre-compute average to normalise */

        for (LatLng point : vertices) {
            longitudeSum += point.longitude;
            latitudeSum += point.latitude;
        }

        longitudeAvg = (longitudeSum / size);
        latitudeAvg = (latitudeSum / size);

        xAvg = longitudeAvg * RADIUS_EARTH_180TH_PI * cosRadian(latitudeAvg);
        yAvg = latitudeAvg * RADIUS_EARTH_180TH_PI;

        /* maths and magic incoming */

        for (int i = 0; i < size; ++i) {
            LatLng point = vertices.get(i);

            double lat = point.latitude;
            double lng = point.longitude;

            double x = lng * RADIUS_EARTH_180TH_PI * cosRadian(lat);
            double xNorm = xAvg - x;

            double y = lat * RADIUS_EARTH_180TH_PI;
            double yNorm = yAvg - y;

            double angle = (Math.atan(yNorm/xNorm)*180) / Math.PI
                    + ((xNorm/Math.abs(xNorm))*-1) * 90
                    + 180;

            coordinates[i] = new Point2D(x, xNorm, y, yNorm, angle);
        }

        // sorting by angle avoids self-intersection of the polygonal path taken
        Arrays.sort(coordinates, (p1, p2) -> {
            double angle1 = p1.angle;
            double angle2 = p2.angle;

            if (angle1 == angle2) return 0;

            return (angle1 > angle2) ? 1 : -1;
        });

        return new ArrayList<>(Arrays.asList(coordinates));
    }

    /**
     * Aims to simulate circular-linked-list with negative indexing.
     *
     * @param index     index of the element to return
     *                  (negative indices move from end downwards)
     *
     * @param list      the list in question
     *
     * @return          the element at position number 'index'
     */
    private static Point2D circularGet(int index, List<Point2D> list) {
        int length = list.size();

        // simulate reach-around
        int k = index % length;

        // simulate negative indexing
        if (k < 0) {
            k = length + k;
        }

        return list.get(k);
    }

    private static double cosRadian(double x) {
        return Math.cos(Math.toRadians(x));
    }

    private static class Point2D {

        double x;
        double xNorm;

        double y;
        double yNorm;

        double angle;

        Point2D(double x, double xNorm, double y, double yNorm, double angle) {
            this.x = x;
            this.xNorm = xNorm;

            this.y = y;
            this.yNorm = yNorm;

            this.angle = angle;
        }
    }
}
