package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.contract;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;

public abstract class AbstractBottomSheetContractBindings
        extends AbstractBottomSheetBase<Contract, ContractHandler> {

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

    @BindView(R.id.bs_contract_toolbar_contract_nr)
    TextView toolbarContractNr;

    @BindView(R.id.bs_contract_toolbar_name)
    TextView toolbarContractName;

    @BindView(R.id.bs_contract_toolbar_area)
    TextView toolbarContractArea;

    @BindView(R.id.bs_contract_toolbar_date)
    TextView toolbarContractDate;

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

    @BindString(R.string.map_frag_bottomsheet_dc_close_dialog_message) //TODO
    String strBottomSheetCloseDialogMessage;

    @Override
    protected String getCloseMessage() {
        return strBottomSheetCloseDialogMessage;
    }

    @BindString(R.string.map_frag_bottomsheet_dc_delete_dialog_message) //TODO
    String strBottomSheetDeleteDialogMessage;

    @Override
    protected String getDeleteMessage() {
        return strBottomSheetDeleteDialogMessage;
    }


    // ### Constructor ################################################################################ Constructor ###

    public AbstractBottomSheetContractBindings(IBottomSheetOwner owner) {
        super(owner);
    }


}
