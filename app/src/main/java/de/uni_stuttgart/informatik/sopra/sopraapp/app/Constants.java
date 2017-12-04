package de.uni_stuttgart.informatik.sopra.sopraapp.app;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Houses app-wide, static constants
 */
public class Constants {

    /* permissions */

    public static final int REQUEST_LOCATION_PERMISSION = 202;
    public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";


    // TODO: remove as soon as polygon-creation feature is implemented
    public static final ArrayList<LatLng> TEST_POLYGON_COORDINATES = new ArrayList<>(
            Arrays.asList(
                    new LatLng(48.808631, 8.849357), new LatLng(48.808304, 8.853308),
                    new LatLng(48.807021, 8.853443), new LatLng(48.807157, 8.851568),
                    new LatLng(48.806494, 8.851383), new LatLng(48.806448, 8.851114),
                    new LatLng(48.806565, 8.850313), new LatLng(48.806940, 8.849134),
                    new LatLng(48.807047, 8.849072)
            )
    );
}
