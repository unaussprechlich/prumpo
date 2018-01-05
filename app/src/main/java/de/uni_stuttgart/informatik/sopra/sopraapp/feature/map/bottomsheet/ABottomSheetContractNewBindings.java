package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public abstract class ABottomSheetContractNewBindings
        extends ABottomSheetBaseFunctions {

    // ### Dimensions ################################################################################## Dimensions ###

    // ### Views ############################################################################################ Views ###

    @BindView(R.id.bs_contract_editText_inputPolicyholder)
    EditText inputPolicyholder;

    @BindView(R.id.bs_contract_editText_inputDamage)
    EditText inputDamages;

    @BindView(R.id.bs_contract_editText_region)
    EditText inputLocation;

    @BindView(R.id.bs_contract_add_damagecase)
    Button buttonAddDamageCase;

    // ### Strings ######################################################################################## Strings ###

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_contract_policyholder_header)
    String strBottomSheetInpDialogPolicyholderHeader;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_contract_policyholder_hint)
    String strBottomSheetInpDialogPolicyholderHint;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_location_header)
    String strBottomSheetInpDialogLocationHeader;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_location_hint)
    String strBottomSheetInpDialogLocationHint;

    @BindString(R.string.map_frag_bs_contract_input_hint_damages)
    String strDamagesHeader;

    @BindArray(R.array.damages)
    String[] allPossibleDamages;


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
