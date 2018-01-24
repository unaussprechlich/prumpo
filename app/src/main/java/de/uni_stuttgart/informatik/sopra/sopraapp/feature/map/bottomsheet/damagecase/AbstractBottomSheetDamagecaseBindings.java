package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.damagecase;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;

public abstract class AbstractBottomSheetDamagecaseBindings extends AbstractBottomSheetBase<DamageCase, DamageCaseHandler> {

    // ### Dimensions ################################################################################## Dimensions ###


    // ### Views ############################################################################################ Views ###

    @BindView(R.id.bs_dc_toolbar_theme)
    TextView toolbarThemeArea;

    @BindView(R.id.bs_dc_toolbar_dc_nr)
    TextView toolbarDamagecaseNr;

    @BindView(R.id.bs_dc_toolbar_name)
    TextView toolbarDamagecaseName;

    @BindView(R.id.bs_dc_toolbar_area)
    TextView toolbarDamagecaseArea;

    @BindView(R.id.bs_dc_toolbar_location)
    TextView toolbarDamagecaseLocation;

    @BindView(R.id.bs_dc_editText_inputDate)
    EditText contentInputDate;

    @BindView(R.id.bs_dc_textView_inputPolicyholder)
    TextView contentPolicyholder;

    @BindView(R.id.bs_dc_textView_inputContractName)
    TextView contentContractName;

    @BindView(R.id.bc_damagecase_progress)
    ProgressBar progressBar;

    // ### Strings ######################################################################################## Strings ###

    @BindString(R.string.map_frag_bottomsheet_dc_close_dialog_message)
    String strBottomSheetCloseDialogMessage;

    @Override
    protected String getCloseMessage() {
        return strBottomSheetCloseDialogMessage;
    }

    @BindString(R.string.map_frag_bottomsheet_dc_delete_dialog_message)
    public String strBottomSheetDeleteDialogMessage;

    @Override
    protected String getDeleteMessage() {
        return strBottomSheetDeleteDialogMessage;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    // ### Colors ########################################################################################## Colors ###

    @BindColor(R.color.map_damagecase_stroke)
    int damagecaseDefaultColor;

    // ### Constructor ################################################################################ Constructor ###

    public AbstractBottomSheetDamagecaseBindings(IBottomSheetOwner owner) {
        super(owner);
    }


}
