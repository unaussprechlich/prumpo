package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public interface IBottomSheet extends BottomSheetListAdapter.ItemCountListener {

    enum TYPE {
        DAMAGE_CASE, DAMAGE_CASE_NEW,
        CONTRACT, CONTRACT_NEW
    }

    void show();

    void close();

    TYPE getType();

    default void displayCurrentAreaValue(Double area) {
    }

    interface OnBottomSheetClose {

        /**
         * Specifies the action after the Bottom Sheet got closed.
         */
        void onBottomSheetClose();
    }

}