package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;


public abstract class MapBindFragment extends DaggerFragment {

    @BindView(R.id.mapView)
    MapView mMapView;

    @BindView(R.id.map_fab_plus)
    FloatingActionButton mMapFabPlus;

    @BindView(R.id.map_fab_locate)
    FloatingActionButton mMapFabLocate;

    @BindView(R.id.bottom_sheet_container)
    NestedScrollView mBSContainer;

    @BindView(R.id.bottom_sheet_toolbar)
    Toolbar mBSToolbar;

    @BindView(R.id.bottom_sheet_recyclerview)
    RecyclerView mBSRecyclerView;

    @BindView(R.id.bottom_sheet_input_title)
    EditText mBSEditTextInputTitle;

    @BindView(R.id.bottom_sheet_input_location)
    EditText mBSEditTextInputLocation;

    @BindView(R.id.bottom_sheet_input_policyholder)
    EditText mBSEditTextInputPolicyholder;

    @BindView(R.id.bottom_sheet_input_expert)
    EditText mBSEditTextInputExpert;

    @BindView(R.id.bottom_sheet_input_date)
    EditText mBSEditTextInputDate;

    @BindView(R.id.bottom_sheet_toolbar_dc_title_value)
    TextView mBSTextViewTitleValue;

    @BindView(R.id.bottom_sheet_toolbar_dc_area_value)
    TextView mBSTextViewAreaValue;

    @BindView(R.id.bottom_sheet_toolbar_dc_date_value)
    TextView mBSTextViewDateValue;

    @BindViews({R.id.bottom_sheet_input_title,
            R.id.bottom_sheet_input_location,
            R.id.bottom_sheet_input_policyholder,
            R.id.bottom_sheet_input_expert,
            R.id.bottom_sheet_input_date})
    List<EditText> damageCaseBottomSheetInputFields;

    @BindString(R.string.map)
    String strAppbarTitle;

    @BindString(R.string.map_frag_botsheet_dialog_dc_name)
    String strBSDialogName;

    @BindString(R.string.map_frag_botsheet_dialog_dc_name_hint)
    String strBSDialogNameHint;

    @BindString(R.string.map_frag_botsheet_dialog_dc_location)
    String strBSDialogDCLocation;

    @BindString(R.string.map_frag_botsheet_dialog_dc_location_hint)
    String strBSDialogDCLocationHint;

    @BindString(R.string.map_frag_botsheet_dialog_dc_policyholder)
    String strBSDialogDCPolicyholder;

    @BindString(R.string.map_frag_botsheet_dialog_dc_policyholder_hint)
    String strBSDialogDCPolicyholderHint;

    @BindString(R.string.map_frag_botsheet_dialog_dc_expert)
    String strBSDialogDCExpert;

    @BindString(R.string.map_frag_botsheet_dialog_dc_expert_hint)
    String strBSDialogDCExpertHint;

    @BindString(R.string.map_frag_botsheet_alert_close_title)
    String strBSDialogCloseTitle;

    @BindString(R.string.map_frag_botsheet_alert_close_text)
    String strBSDialogCloseText;

    @BindString(R.string.map_frag_botsheet_alert_delete_title)
    String strBSDialogDeleteTitle;

    @BindString(R.string.map_frag_botsheet_alert_delete_text)
    String strBSDialogDeleteText;

    @BindString(R.string.prompt_enable_localization)
    String strPromptEnableLocation;

    @BindString(R.string.map_frag_botsheet_alert_yes)
    String strBSDialogCloseOk;

    @BindString(R.string.map_frag_botsheet_alert_no)
    String strBSDialogCloseCancel;

    @BindString(R.string.map_fab_no_gps)
    String sirNoPositionDatesFound;

    @BindString(R.string.map_fab_messages_latitude)
    String strLatitude;

    @BindString(R.string.map_fab_messages_longitude)
    String strLongitude;

    @BindString(R.string.map_frag_botsheet_dialog_dc_date_pattern)
    String simpleDateFormatPattern;

    @BindDrawable(R.drawable.ic_my_location_black_24dp)
    Drawable currentLocationKnownDrawable;

    @BindDrawable(R.drawable.ic_location_disabled_black_24dp)
    Drawable currentLocationUnknownDrawable;
}
