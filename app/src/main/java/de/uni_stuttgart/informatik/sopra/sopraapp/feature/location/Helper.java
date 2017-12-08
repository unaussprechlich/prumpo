package de.uni_stuttgart.informatik.sopra.sopraapp.feature.location;

import android.arch.persistence.room.TypeConverter;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.LOCATION_SUPPLIER_BASE;

/**
 * Helper methods regarding location services.
 */
public class Helper {

    private static Random random = new Random();

    @TypeConverter
    public static LatLng latLngOf(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static LatLng getRandomLatLng() {
        LatLng base = Constants.LOCATION_SUPPLIER_BASE;

        double offset1 = random.nextDouble() * 0.001;
        double offset2 = random.nextDouble() * 0.001;

        return new LatLng(base.latitude + offset1, base.longitude + offset2);
    }

}
