package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public abstract class ABottomSheetContractNewBindings
        extends ABottomSheetBaseFunctions{

    // ### Dimensions ################################################################################## Dimensions ###
    // ### Views ############################################################################################ Views ###
    // ### Strings ######################################################################################## Strings ###

    // ### Constructor ################################################################################ Constructor ###

    public ABottomSheetContractNewBindings(Context context,
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


}
