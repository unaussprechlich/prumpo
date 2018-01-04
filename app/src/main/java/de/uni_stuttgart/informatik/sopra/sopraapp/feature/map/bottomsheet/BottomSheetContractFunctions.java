package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public class BottomSheetContractFunctions extends BottomSheetContractNewFunctions {

    private Contract contract;

    public BottomSheetContractFunctions(Context context,
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

    @Override
    public TYPE getType() {
        return TYPE.INSURANCE;
    }
}
