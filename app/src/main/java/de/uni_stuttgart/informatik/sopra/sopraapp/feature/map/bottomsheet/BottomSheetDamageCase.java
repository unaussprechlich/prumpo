package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public class BottomSheetDamageCase extends BottomSheetNewDamageCase {

    public BottomSheetDamageCase(Context context,
                                 NestedScrollView nestedScrollView,
                                 LockableBottomSheetBehaviour bottomSheetBehavior,
                                 DamageCaseHandler damageCaseHandler,
                                 Lifecycle lifecycle,
                                 GpsService gpsService,
                                 SopraMap sopraMap,
                                 DamageCase damageCase,
                                 OnBottomSheetClose onBottomSheetClose) {

        super(context,
                nestedScrollView,
                bottomSheetBehavior,
                damageCaseHandler,
                lifecycle,
                gpsService,
                sopraMap,
                onBottomSheetClose);

        tbDeleteButton.setVisible(true);

        String roundedArea = String.valueOf((double) Math.round(damageCase.getAreaSize() * 100d) / 100d);
        mBottomSheetToolbarViewArea.setText(roundedArea);
        mBottomSheetInputTitle.setText(damageCase.getName());
        mBottomSheetToolbarViewTitle.setText(damageCase.getName());
        mBottomSheetInputLocation.setText(damageCase.getAreaCode());
        mBottomSheetInputPolicyholder.setText(damageCase.getNamePolicyholder());
        mBottomSheetInputExpert.setText(damageCase.getExpertID());
        mBottomSheetInputDate.setText(damageCase.getDate().toString(strSimpleDateFormatPattern));
        mBottomSheetToolbarViewDate.setText(damageCase.getDate().toString(strSimpleDateFormatPattern));
        mBottomSheetDate = damageCase.getDate();

    }

    @Override
    public TYPE getType() {
        return TYPE.DAMAGE_CASE;
    }

}
