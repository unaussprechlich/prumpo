package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public class BottomSheetContract extends BottomSheetContractNew {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    private Contract contract;

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetContract(Context context,
                               NestedScrollView nestedScrollView,
                               LockableBottomSheetBehaviour lockableBottomSheetBehaviour,
                               Lifecycle lifecycle,
                               GpsService gpsService,
                               SopraMap sopraMap,
                               OnBottomSheetClose onBottomSheetClose,
                               Contract contract) {

        super(context,
                nestedScrollView,
                lockableBottomSheetBehaviour,
                lifecycle,
                gpsService,
                sopraMap,
                onBottomSheetClose);

        this.contract = contract;
    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    public TYPE getType() {
        return TYPE.INSURANCE;
    }
}
