package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public class BottomSheetContractNewFunctions extends ABottomSheetContractNewBindings {

    public BottomSheetContractNewFunctions(Context context,
                                           NestedScrollView nestedScrollView,
                                           LockableBottomSheetBehaviour lockableBottomSheetBehaviour,
                                           Lifecycle lifecycle,
                                           GpsService gpsService,
                                           SopraMap sopraMap,
                                           OnBottomSheetClose onBottomSheetClose) {

        super(context,
                nestedScrollView,
                lockableBottomSheetBehaviour,
                lifecycle,
                gpsService,
                sopraMap,
                onBottomSheetClose);
    }

    @Override
    int getLayoutResourceFile() {
        return R.layout.activity_main_bs_damagecase;
    }

    @Override
    void onToolbarSaveButtonPressed() {

    }

    @Override
    void onToolbarDeleteButtonPressed() {

    }

    @Override
    void onToolbarCloseButtonPressed() {

    }

    @Override
    public TYPE getType() {
        return TYPE.INSURANCE_NEW;
    }
}
