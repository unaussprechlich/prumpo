package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events;

import com.google.android.gms.maps.model.LatLng;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;

public class EventsVertex {

    private EventsVertex() {
        // no need to instantiate outer event
    }

    public static class Selected {
        public int vertexNumber;

        public Selected(int vertexNumber) {
            this.vertexNumber = vertexNumber;
        }
    }

    public static class Deleted {
        public int vertexNumber;

        public Deleted(int vertexNumber) {
            this.vertexNumber = vertexNumber;
        }
    }

    public static class Created {
        public PolygonType polygonType;
        public LatLng position;

        public Created(LatLng position, PolygonType polygonType) {
            this.polygonType = polygonType;
            this.position = position;
        }
    }
}
