package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

/**
 * Defines all Butter Knife bindings which are used in all Bottom Sheets.
 */
public abstract class AbstractBottomSheetBaseBindings {

    // ### Dimensions ################################################################################## Dimensions ###

    @BindDimen(R.dimen.bottomsheet_bubblelist_height)
    int dimenBottomSheetBubbleListHeight;

    @BindDimen(R.dimen.bottomsheet_toolbar_height)
    int dimenBottomSheetToolbarHeight;

    @BindDimen(R.dimen.bottomsheet_peek_height)
    int dimenBottomSheetPeekHeight;

    @BindDimen(R.dimen.bottomsheet_height)
    int dimenBottomSheetHeight;


    // ### Views ############################################################################################ Views ###

    @BindView(R.id.bottom_sheet_toolbar)
    Toolbar viewBottomSheetToolbar;

    @BindView(R.id.bottom_sheet_bubblelist)
    RecyclerView viewBottomSheetBubbleList;


    // ### Strings ######################################################################################## Strings ###

    @BindString(R.string.map_frag_bottomsheet_close_dialog_header)
    String strBottomSheetCloseDialogHeader;

    @BindString(R.string.map_frag_bottomsheet_delete_dialog_header)
    String strBottomSheetDeleteDialogHeader;

    @BindString(R.string.map_frag_botsheet_dialog_yes)
    String strBottomSheetCloseDialogOk;

    @BindString(R.string.map_frag_botsheet_dialog_no)
    String strBottomSheetCloseDialogCancel;

    @BindString(R.string.map_frag_bottomsheet_date_pattern)
    protected
    String strSimpleDateFormatPattern;

    @BindString(R.string.today)
    protected
    String strToday;

    @BindString(R.string.map_frag_botsheet_dialog_positive)
    protected
    String strBottomSheetDialogPositive;

    @BindString(R.string.map_frag_botsheet_dialog_negative)
    protected
    String strBottomSheetDialogNegative;



}
