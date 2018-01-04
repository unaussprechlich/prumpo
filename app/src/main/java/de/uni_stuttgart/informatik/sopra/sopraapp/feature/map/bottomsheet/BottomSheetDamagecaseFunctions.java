package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public class BottomSheetDamagecaseFunctions extends BottomSheetContractNewFunctions {

    private DamageCase damageCase;

    public BottomSheetDamagecaseFunctions(Context context,
                                          NestedScrollView nestedScrollView,
                                          LockableBottomSheetBehaviour lockableBottomSheetBehaviour,
                                          Lifecycle lifecycle,
                                          GpsService gpsService,
                                          SopraMap sopraMap,
                                          OnBottomSheetClose onBottomSheetClose,
                                          DamageCase damageCase) {

        super(context,
                nestedScrollView,
                lockableBottomSheetBehaviour,
                lifecycle,
                gpsService,
                sopraMap,
                onBottomSheetClose);
    }

    @Override
    public TYPE getType() {
        return TYPE.DAMAGE_CASE;
    }
}
