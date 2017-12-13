package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;


public abstract class MapBindFragment extends DaggerFragment {

    static final ButterKnife.Action<EditText> REMOVE_ERRORS =
            (editText, index) -> editText.setError(null);
    static final ButterKnife.Action<TextView> REMOVE_TEXT =
            (editText, index) -> editText.setText("");
    @BindView(R.id.mapView)
    MapView mMapView;
    @BindView(R.id.fab_locate)
    FloatingActionButton mFabLocate;

    @BindString(R.string.appbar_title_map)
    String strAppbarTitle;
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
    @BindString(R.string.prompt_enable_localization)
    String strPromptEnableLocation;
    @BindString(R.string.map_frag_botsheet_dialog_yes)
    String strBottomSheetCloseDialogOk;
    @BindString(R.string.map_frag_botsheet_dialog_no)
    String strBottomSheetCloseDialogCancel;
    @BindString(R.string.map_fab_no_gps)
    String strNoLocationDatesFound;
    @BindString(R.string.map_fab_messages_latitude)
    String strLatitude;
    @BindString(R.string.map_fab_messages_longitude)
    String strLongitude;
    @BindString(R.string.map_frag_bottomsheet_date_pattern)
    String strSimpleDateFormatPattern;
    @BindDrawable(R.drawable.ic_my_location_black_24dp)
    Drawable currentLocationKnownDrawable;
    @BindDrawable(R.drawable.ic_location_disabled_black_24dp)
    Drawable currentLocationUnknownDrawable;
    @BindDimen(R.dimen.bottomsheet_bubblelist_height)
    int dimenBottomSheetBubbleListHeight;
    @BindDimen(R.dimen.bottomsheet_toolbar_height)
    int dimenBottomSheetToolbarHeight;

    @BindDimen(R.dimen.bottomsheet_peek_height)
    int dimenBottomSheetPeekHeight;

    @BindDimen(R.dimen.bottomsheet_height)
    int dimenBottomSheetHeight;
}
