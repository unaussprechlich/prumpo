package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.content.Context;
import android.location.Location;
import android.view.Gravity;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicBoolean;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.Helper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.VertexCreated;

public class OnAddButtonLocationCallback implements LocationCallbackListener {

    private Context context;
    private AtomicBoolean callbackDone;

    public OnAddButtonLocationCallback(Context context, AtomicBoolean callbackDone) {
        this.context = context;
        this.callbackDone = callbackDone;
    }

    @Override
    public void onLocationFound(Location location) {
        callbackDone.set(true);

        EventBus.getDefault().post(new VertexCreated(Helper.latLngOf(location)));
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
