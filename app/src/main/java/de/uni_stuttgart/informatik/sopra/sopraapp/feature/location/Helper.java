package de.uni_stuttgart.informatik.sopra.sopraapp.feature.location;

import android.arch.persistence.room.TypeConverter;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Helper methods regarding location services.
 */
public class Helper {

    @TypeConverter
    public static LatLng latLngOf(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

}
