package de.uni_stuttgart.informatik.sopra.sopraapp.feature.location;

import android.location.Location;

/**
 * @author Alexander Zeising,
 */

public interface LocationCallbackListener {

    void onLocationFound(Location location);

    void onLocationNotFound();
}
