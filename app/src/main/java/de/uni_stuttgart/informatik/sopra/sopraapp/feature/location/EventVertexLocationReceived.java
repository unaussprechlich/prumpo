package de.uni_stuttgart.informatik.sopra.sopraapp.feature.location;

import com.google.android.gms.maps.model.LatLng;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;

public class EventVertexLocationReceived {

    public LatLng position;
    public PolygonType polygonType;

    public EventVertexLocationReceived(LatLng position, PolygonType polygonType) {
        this.position = position;
        this.polygonType = polygonType;
    }
}
