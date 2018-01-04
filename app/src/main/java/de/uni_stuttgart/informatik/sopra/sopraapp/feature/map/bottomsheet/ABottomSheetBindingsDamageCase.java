package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import org.joda.time.DateTime;

import java.util.List;

public abstract class ABottomSheetBindingsDamageCase {

    static final ButterKnife.Action<EditText> REMOVE_ERRORS =
            (editText, index) -> editText.setError(null);
    static final ButterKnife.Action<TextView> REMOVE_TEXT =
            (editText, index) -> editText.setText("");

    @BindView(R.id.bottom_sheet_dc_input_title)
    EditText mBottomSheetInputTitle;

    @BindView(R.id.bottom_sheet_dc_input_location)
    EditText mBottomSheetInputLocation;

    @BindView(R.id.bottom_sheet_dc_input_policyholder)
    EditText mBottomSheetInputPolicyholder;

    @BindView(R.id.bottom_sheet_dc_input_date)
    EditText mBottomSheetInputDate;

    @BindViews({R.id.bottom_sheet_dc_input_title,
            R.id.bottom_sheet_dc_input_location,
            R.id.bottom_sheet_dc_input_policyholder,
            R.id.bottom_sheet_dc_input_date})
    List<EditText> mBottomSheetInputs;


    @BindString(R.string.map_frag_botsheet_toolbar_title_dc)
    String strToolbarBottomSheetTitle;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_title_header)
    String strBottomSheetInpDialogTitleHeader;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_title_hint)
    String strBottomSheetInpDialogTitleHint;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_location_header)
    String strBottomSheetInpDialogLocationHeader;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_location_hint)
    String strBottomSheetInpDialogLocationHint;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_policyholder_header)
    String strBottomSheetInpDialogPolicyholderHeader;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_policyholder_hint)
    String strBottomSheetInpDialogPolicyholderHint;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_expert_header)
    String strBottomSheetInpDialogExpertHeader;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_dc_expert_hint)
    String strBottomSheetInpDialogExpertHint;

    @BindString(R.string.map_frag_bottomsheet_dc_close_dialog_message)
    String strBottomSheetCloseDialogMessage;

    @BindString(R.string.map_frag_bottomsheet_dc_delete_dialog_message)
    String strBottomSheetDeleteDialogMessage;

    DateTime mBottomSheetDate = DateTime.now();


//    ABottomSheetBindingsDamageCase(Context context,
//                                   NestedScrollView bottomSheetContainer,
//                                   LockableBottomSheetBehaviour bottomSheetBehavior,
//                                   Lifecycle lifecycle,
//                                   GpsService gpsService,
//                                   SopraMap sopraMap,
//                                   OnBottomSheetClose onBottomSheetClose) {
//
//        super(context,
//                bottomSheetContainer,
//                bottomSheetBehavior,
//                lifecycle,
//                gpsService,
//                sopraMap,
//                onBottomSheetClose);
//    }
}
