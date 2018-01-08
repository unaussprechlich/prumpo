package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events;

import android.support.annotation.Nullable;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;

public class EventOpenMapFragment {

    public Class targetBottomSheet;

    public <Model extends ModelDB> EventOpenMapFragment(@Nullable Class<Model> targetBottomSheet) {
        this.targetBottomSheet = targetBottomSheet;
    }

}
