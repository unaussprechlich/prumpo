package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.widget.EditText;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public abstract class ABottomSheetBindingsInsurance extends ABottomSheetBase {

    @BindView(R.id.bottom_sheet_input_insurance_name)
    EditText mBottomSheetInputInsuranceName;


    @BindString(R.string.map_frag_bottomsheet_inp_dialog_insurance_title_header)
    String strBottomSheetInpDialogInsuranceNameHeader;

    @BindString(R.string.map_frag_bottomsheet_inp_dialog_insurance_title_hint)
    String strBottomSheetInpDialogInsuranceNameHint;

    @BindString(R.string.map_frag_botsheet_toolbar_title_insurance)
    String strToolbarBottomSheetTitle;

    @BindString(R.string.map_frag_bottomsheet_insurance_close_dialog_message)
    String strBottomSheetCloseDialogMessage;

    @BindString(R.string.map_frag_bottomsheet_insurance_delete_dialog_message)
    String strBottomSheetDeleteDialogMessage;

    ABottomSheetBindingsInsurance(Context context,
                                  NestedScrollView bottomSheetContainer,
                                  LockableBottomSheetBehaviour bottomSheetBehavior,
                                  Lifecycle lifecycle,
                                  GpsService gpsService,
                                  SopraMap sopraMap,
                                  OnBottomSheetClose onBottomSheetClose) {

        super(context,
                bottomSheetContainer,
                bottomSheetBehavior,
                lifecycle,
                gpsService,
                sopraMap,
                onBottomSheetClose);
    }
}
