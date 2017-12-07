package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.content.Context;
import android.location.Location;
import android.view.Gravity;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.Helper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.VertexCreated;

public class OnAddButtonLocationCallback implements LocationCallbackListener {

    private Context context;

    public OnAddButtonLocationCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onLocationFound(Location location) {
        EventBus.getDefault().post(new VertexCreated(Helper.latLngOf(location)));
    }

    @Override
    public void onLocationNotFound() {
        Toast toast = Toast.makeText(
                context,
                "Es konnte keine gültige Position ermittelt werden.",
                Toast.LENGTH_SHORT
        );

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
