package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper methods of the location service.
 */

public class Helper {

    // the radius of the earth is approximated in Kilometres (6371 km)
    private static final double RADIUS_EARTH_180TH_PI = Math.PI * 6371/180;

    /**
     * Estimate the area under a polygonal section of the earth, given it's LatLng vertices.
     *
     * @param vertices      LatLng coordinates of the vertices as <src>List</src>(, in any order!)
     *
     * @return              approximated area in m²
     */
    public static double areaOfPolygon(List<LatLng> vertices) {

        double areaSum = 0;

        ArrayList<Point2D> coordinates = projectAndNormalize(vertices);

        for (int i = 0; i < coordinates.size(); ++i) {
            double y = circularGet(i, coordinates).y;
            double lastX = circularGet(i-1, coordinates).x;
            double beforeLastY = circularGet(i-2, coordinates).y;

            areaSum += lastX * (y - beforeLastY);
        }

        return Math.abs(areaSum/2)*1000000;
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
