package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events;

import com.google.android.gms.maps.model.LatLng;

public class VertexCreated {

    public LatLng position;

    public VertexCreated(LatLng position) {
        this.position = position;
    }
}
