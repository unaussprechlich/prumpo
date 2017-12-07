package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events;

import android.arch.lifecycle.LiveData;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;



public  class DamageCaseEvent{

    public static class Base{

        public Base(LiveData<DamageCase> damageCase) {
            this.damageCase = damageCase;
        }

        public final LiveData<DamageCase> damageCase;
    }

    public static class Created extends Base {

        public Created(LiveData<DamageCase>  damageCase) {
            super(damageCase);
        }
    }

    public class Deleted extends Base {

        public Deleted(LiveData<DamageCase>  damageCase) {
            super(damageCase);
        }
    }

    public static class Saved extends Base {

        public Saved(LiveData<DamageCase>  damageCase) {
            super(damageCase);
        }
    }
}


