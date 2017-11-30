package de.uni_stuttgart.informatik.sopra.sopraapp.service.location;

import android.location.Location;

/**
 * @author Alexander Zeising,
 */

public interface LocationCallbackListener {

    void onLocationFound(Location location);

    void onLocationNotFound();
}
