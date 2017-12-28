package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.*;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import org.joda.time.DateTime;

import java.util.List;

abstract public class ABottomSheetBindings {

    static final ButterKnife.Action<EditText> REMOVE_ERRORS =
            (editText, index) -> editText.setError(null);
    static final ButterKnife.Action<TextView> REMOVE_TEXT =
            (editText, index) -> editText.setText("");

    @BindDimen(R.dimen.bottomsheet_bubblelist_height)
    int dimenBottomSheetBubbleListHeight;
    @BindDimen(R.dimen.bottomsheet_toolbar_height)
    int dimenBottomSheetToolbarHeight;
    @BindDimen(R.dimen.bottomsheet_peek_height)
    int dimenBottomSheetPeekHeight;
    @BindDimen(R.dimen.bottomsheet_height)
    int dimenBottomSheetHeight;

    @BindString(R.string.map_frag_botsheet_dialog_yes)
    String strBottomSheetCloseDialogOk;
    @BindString(R.string.map_frag_botsheet_dialog_no)
    String strBottomSheetCloseDialogCancel;

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
    @BindString(R.string.map_frag_bottomsheet_close_dialog_header)
    String strBottomSheetCloseDialogHeader;
    @BindString(R.string.map_frag_bottomsheet_close_dialog_message)
    String strBottomSheetCloseDialogMessage;
    @BindString(R.string.map_frag_bottomsheet_delete_dialog_header)
    String strBottomSheetDeleteDialogHeader;
    @BindString(R.string.map_frag_bottomsheet_delete_dialog_message)
    String strBottomSheetDeleteDialogMessage;
    @BindString(R.string.map_frag_bottomsheet_date_pattern)
    String strSimpleDateFormatPattern;

    DateTime mBottomSheetDate = DateTime.now();
    @BindView(R.id.bottom_sheet_container_all)
    CoordinatorLayout mBottomSheetLayoutContainer;
    @BindView(R.id.bottom_sheet_toolbar)
    Toolbar mBottomSheetToolbar;
    @BindView(R.id.bottom_sheet_bubblelist)
    RecyclerView mBottomSheetBubbleList;
    @BindView(R.id.bottom_sheet_input_title)
    EditText mBottomSheetInputTitle;
    @BindView(R.id.bottom_sheet_input_location)
    EditText mBottomSheetInputLocation;
    @BindView(R.id.bottom_sheet_input_policyholder)
    EditText mBottomSheetInputPolicyholder;
    @BindView(R.id.bottom_sheet_input_expert)
    EditText mBottomSheetInputExpert;
    @BindView(R.id.bottom_sheet_input_date)
    EditText mBottomSheetInputDate;
    @BindView(R.id.bottom_sheet_toolbar_dc_title_value)
    TextView mBottomSheetToolbarViewTitle;
    @BindView(R.id.bottom_sheet_toolbar_dc_area_value)
    TextView mBottomSheetToolbarViewArea;
    @BindView(R.id.bottom_sheet_toolbar_dc_date_value)
    TextView mBottomSheetToolbarViewDate;
    @BindViews({R.id.bottom_sheet_input_title,
            R.id.bottom_sheet_input_location,
            R.id.bottom_sheet_input_policyholder,
            R.id.bottom_sheet_input_expert,
            R.id.bottom_sheet_input_date})
    List<EditText> mBottomSheetInputs;

    @BindString(R.string.map_frag_botsheet_toolbar_title)
    String strToolbarBottomSheetTitle;
}
