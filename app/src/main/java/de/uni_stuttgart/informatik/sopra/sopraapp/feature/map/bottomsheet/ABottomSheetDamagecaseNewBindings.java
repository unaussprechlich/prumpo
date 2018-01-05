package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public abstract class ABottomSheetDamagecaseNewBindings<T> extends ABottomSheetBaseFunctions<T> {

    // ### Dimensions ################################################################################## Dimensions ###


    // ### Views ############################################################################################ Views ###

    @BindView(R.id.bs_dc_toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.bs_dc_toolbar_area)
    TextView toolbarArea;

    @BindView(R.id.bs_dc_toolbar_date)
    TextView toolbarDate;

    @BindView(R.id.bs_dc_editText_inputDate)
    EditText contentInputDate;

    @BindView(R.id.bs_dc_textView_inputPolicyholder)
    TextView contentPolicyholder;

    @BindView(R.id.bs_dc_textView_inputContractName)
    TextView contentContractName;

    // ### Strings ######################################################################################## Strings ###

    @BindString(R.string.map_frag_bottomsheet_dc_close_dialog_message)
    String strBottomSheetCloseDialogMessage;

    @BindString(R.string.map_frag_bottomsheet_dc_delete_dialog_message)
    String strBottomSheetDeleteDialogMessage;

    // ### Constructor ################################################################################ Constructor ###

    public ABottomSheetDamagecaseNewBindings(Context context,
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
