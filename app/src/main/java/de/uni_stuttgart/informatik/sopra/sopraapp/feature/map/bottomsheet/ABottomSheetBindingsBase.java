package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

abstract public class ABottomSheetBindingsBase {

    @BindDimen(R.dimen.bottomsheet_bubblelist_height)
    int dimenBottomSheetBubbleListHeight;

    @BindDimen(R.dimen.bottomsheet_toolbar_height)
    int dimenBottomSheetToolbarHeight;

    @BindDimen(R.dimen.bottomsheet_peek_height)
    int dimenBottomSheetPeekHeight;

    @BindDimen(R.dimen.bottomsheet_height)
    int dimenBottomSheetHeight;


    @BindView(R.id.bottom_sheet_container_all)
    CoordinatorLayout mBottomSheetLayoutContainer;

    @BindView(R.id.bottom_sheet_toolbar)
    Toolbar mBottomSheetToolbar;

    @BindView(R.id.bottom_sheet_bubblelist)
    RecyclerView mBottomSheetBubbleList;

    @BindView(R.id.bottom_sheet_toolbar_dc_title_value)
    TextView mBottomSheetToolbarViewTitle;

    @BindView(R.id.bottom_sheet_toolbar_dc_area_value)
    TextView mBottomSheetToolbarViewArea;

    @BindView(R.id.bottom_sheet_toolbar_dc_date_value)
    TextView mBottomSheetToolbarViewDate;


    @BindString(R.string.map_frag_bottomsheet_close_dialog_header)
    String strBottomSheetCloseDialogHeader;

    @BindString(R.string.map_frag_bottomsheet_delete_dialog_header)
    String strBottomSheetDeleteDialogHeader;

    @BindString(R.string.map_frag_botsheet_dialog_yes)
    String strBottomSheetCloseDialogOk;

    @BindString(R.string.map_frag_botsheet_dialog_no)
    String strBottomSheetCloseDialogCancel;

    @BindString(R.string.map_frag_bottomsheet_date_pattern)
    String strSimpleDateFormatPattern;


}
