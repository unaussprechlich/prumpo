package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events;

public abstract class EventsPolygonSelected {

    public long uniqueId;

    private EventsPolygonSelected(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public static class DamageCase extends EventsPolygonSelected {
        public DamageCase(long uniqueId) {
            super(uniqueId);
        }
    }

    public static class InsuranceCoverage extends EventsPolygonSelected {
        public InsuranceCoverage(long uniqueId) {
            super(uniqueId);
        }
    }

}
