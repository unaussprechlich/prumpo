package de.uni_stuttgart.informatik.sopra.sopraapp.app;


import com.google.android.gms.maps.model.LatLng;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

/**
 * Houses app-wide, static constants
 */
public class Constants {
    /* permissions */

    public static final int REQUEST_LOCATION_PERMISSION = 202;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 364;

    /* regex */

    public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public static final LatLng LOCATION_SUPPLIER_BASE = new LatLng(48.743488, 9.091318);


    public static int[] PROFILE_IMAGE_RESOURCES = new int[]{
            R.drawable.zprofile_1,
            R.drawable.zprofile_2,
            R.drawable.zprofile_3,
            R.drawable.zprofile_4,
            R.drawable.zprofile_5,
            R.drawable.zprofile_6,
            R.drawable.zprofile_7,
            R.drawable.zprofile_8,
            R.drawable.zprofile_9,
            R.drawable.zprofile_10,
            R.drawable.zprofile_11,
            R.drawable.zprofile_12,
            R.drawable.zprofile_13,
            R.drawable.zprofile_14,
            R.drawable.zprofile_15,
            R.drawable.zprofile_16,
            R.drawable.zprofile_17,
            R.drawable.zprofile_18,
            R.drawable.zprofile_19,
            R.drawable.zprofile_20,
            R.drawable.zprofile_21,
            R.drawable.zprofile_22,
            R.drawable.zprofile_23,
            R.drawable.zprofile_24,
            R.drawable.zprofile_25,
            R.drawable.zprofile_26,
            R.drawable.zprofile_27,
            R.drawable.zprofile_28,
            R.drawable.zprofile_29
    };

}
