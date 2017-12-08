package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events;

public abstract class PolygonSelected {
    public long uniqueId;

    public PolygonSelected(long uniqueId) {
        this.uniqueId = uniqueId;
    }
}
