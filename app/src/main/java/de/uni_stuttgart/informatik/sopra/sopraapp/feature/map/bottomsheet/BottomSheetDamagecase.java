package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public class BottomSheetDamagecase extends BottomSheetDamagecaseNew {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    private DamageCaseHandler damageCaseHandler;
    private DamageCase damageCase;

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetDamagecase(Context context,
                                 NestedScrollView nestedScrollView,
                                 LockableBottomSheetBehaviour lockableBottomSheetBehaviour,
                                 DamageCaseHandler damageCaseHandler,
                                 Lifecycle lifecycle,
                                 GpsService gpsService,
                                 SopraMap sopraMap,
                                 OnBottomSheetClose onBottomSheetClose,
                                 DamageCase damageCase) {

        super(context,
                nestedScrollView,
                lockableBottomSheetBehaviour,
                damageCaseHandler,
                lifecycle,
                gpsService,
                sopraMap,
                onBottomSheetClose);

        this.damageCaseHandler = damageCaseHandler;
        this.damageCase = damageCase;

        this.tbDeleteButton.setVisible(true);

        displayCurrentAreaValue(damageCase.getAreaSize());
    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    public TYPE getType() {
        return TYPE.DAMAGE_CASE;
    }
}
