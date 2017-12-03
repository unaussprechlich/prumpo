package de.uni_stuttgart.informatik.sopra.sopraapp.app;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Houses app-wide, static constants
 */

public class Constants {

    public static final int REQUEST_LOCATION_PERMISSION = 202;

    // TODO: remove as soon as polygon-creation feature is implemented
    public static final ArrayList<LatLng> TEST_POLYGON_COORDINATES = new ArrayList<>(
            Arrays.asList(
                    new LatLng(48.808631, 8.849357), new LatLng(48.808304, 8.853308),
                    new LatLng(48.807021, 8.853443), new LatLng(48.807157, 8.851568),
                    new LatLng(48.806494, 8.851383), new LatLng(48.806448, 8.851114),
                    new LatLng(48.806565, 8.850313), new LatLng(48.806940, 8.849134),
                    new LatLng(48.807047, 8.849072), new LatLng(48.808631, 8.849357)
            )
    );
}
