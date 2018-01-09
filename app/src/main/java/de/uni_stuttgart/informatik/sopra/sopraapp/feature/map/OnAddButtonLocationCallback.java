package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.content.Context;
import android.location.Location;
import android.view.Gravity;
import android.widget.Toast;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.Helper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsVertex;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;
import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicBoolean;

public class OnAddButtonLocationCallback implements LocationCallbackListener {

    private Context context;
    private AtomicBoolean callbackDone;
    private PolygonType polygonType;

    public OnAddButtonLocationCallback(Context context, AtomicBoolean callbackDone, PolygonType polygonType) {
        this.context = context;
        this.callbackDone = callbackDone;
    }

    @Override
    public void onLocationFound(Location location) {
        callbackDone.set(true);

        EventBus.getDefault().post(new EventsVertex.Created(Helper.latLngOf(location), polygonType));
    }

    @Override
    public void onLocationNotFound() {
        callbackDone.set(true);

        Toast toast = Toast.makeText(
                context,
                "Es konnte keine gültige Position ermittelt werden.",
                Toast.LENGTH_SHORT
        );

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
