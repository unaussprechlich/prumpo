package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.TEST_POLYGON_COORDINATES;

public class MapFragment
        extends DaggerFragment
        implements FragmentBackPressed, LocationCallbackListener {

    @Inject GpsService gpsService;
    @Inject DamageCaseRepository damageCaseRepository;
    @Inject DamageCaseHandler damageCaseHandler;
    @Inject UserManager userManager;

    // TODO: cover case of lost ACCESS_FINE_LOCATION permissions during runtime
    // TODO: replace remaining onClickListeners with ButterKnife annotations

    static final ButterKnife.Action<EditText> REMOVE_ERRORS =
            (editText, index) -> editText.setError(null);

    static final ButterKnife.Action<TextView> REMOVE_TEXT =
            (editText, index) -> editText.setText("");

    static final ButterKnife.Action<TextView> REMOVE_VISIBILITY =
            (textView, index) -> textView.setVisibility(View.INVISIBLE);

    /* Knife-N'-Butter section!' */

    View mRootView;
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

    @BindView(R.id.bottom_sheet_toolbar_dc_title)
    TextView mBSTextViewTitle;

    @BindView(R.id.bottom_sheet_toolbar_dc_area)
    TextView mBSTextViewArea;

    @BindViews({R.id.bottom_sheet_input_title,
            R.id.bottom_sheet_input_location,
            R.id.bottom_sheet_input_policyholder,
            R.id.bottom_sheet_input_expert,
            R.id.bottom_sheet_input_date})
    List<EditText> damageCaseBottomSheetInputFields;

    @BindViews({R.id.bottom_sheet_toolbar_dc_title_value,
            R.id.bottom_sheet_toolbar_dc_area_value,
            R.id.bottom_sheet_toolbar_dc_title,
            R.id.bottom_sheet_toolbar_dc_area})
    List<TextView> damageCaseBottomSheetToolbarFields;

    @BindViews({R.id.bottom_sheet_toolbar_dc_title_value,
            R.id.bottom_sheet_toolbar_dc_area_value})
    List<TextView> damageCaseBottomSheetToolbarUserInserted;

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

    DateTime mBSEditDate = DateTime.now();

    /**
     * The adapter for the horizontal recycler view
     */
    BottomSheetListAdapter bottomSheetListAdapter;
    MenuItem tbCloseButton;
    MenuItem tbDeleteButton;
    ActionMenuItemView tbSaveButton;

    private BottomSheetExpandHandler bottomSheetExpandHandler;

    private SopraMap sopraMap;
    private boolean isGpsServiceBound;

    /**
     * The provided bottom sheet behaviour object
     */
    private LockableBottomSheetBehaviour mBottomSheetBehavior;
    private DateTime damageCaseDate = DateTime.now();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // guard clause for 2nd visit
        if (mRootView != null) return mRootView;

        mRootView = inflater.inflate(R.layout.activity_main_fragment_mapview, container, false);
        ButterKnife.bind(this, mRootView);

        // Set title of app bar
        getActivity().setTitle(strAppbarTitle);

        // create bottom sheet behaviour
        mBottomSheetBehavior = LockableBottomSheetBehaviour.from(mBSContainer);

        // init
        initMapView(savedInstanceState);
        initBottomSheet();

        onResume();

        damageCaseHandler.getLiveData().observe(getActivity(), this::updateDamageCase);

        return mRootView;
    }

    private void updateDamageCase(DamageCase damageCase){
        if(damageCase == null){
            closeBottomSheet();
            return;
        }

        openDamageCase();

        mBSEditTextInputTitle.setText(damageCase.getNameDamageCase());
        mBSEditTextInputLocation.setText(damageCase.getAreaCode());
        mBSEditTextInputPolicyholder.setText(damageCase.getNamePolicyholder());
        mBSEditTextInputExpert.setText(damageCase.getNameExpert());
        mBSEditTextInputDate.setText(damageCase.getDate().toString(simpleDateFormatPattern));
        mBSEditDate = damageCase.getDate();

        for (LatLng latLng : damageCase.getCoordinates()) {
            bottomSheetListAdapter.add();
        }
    }

    private void initMapView(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);

        // to assure immediate display
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(googleMap -> {
            sopraMap = new SopraMap(googleMap, getContext());

            //sopraMap.drawPolygonOf(TEST_POLYGON_COORDINATES, PolygonType.INSURANCE_COVERAGE, "1");
            sopraMap.mapCameraJump(TEST_POLYGON_COORDINATES);

        });

    }

    private void initBottomSheet() {

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheetContainer, int newState) {

                ((MainActivity) getActivity()).setDrawerEnabled(newState == BottomSheetBehavior.STATE_HIDDEN);

                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    onBottomSheetIsHidden(bottomSheetContainer);
                else if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    onBottomSheetCollapsed(bottomSheetContainer);
            }

            @Override
            public void onSlide(@NonNull View bottomSheetContainer, float slideOffset) {

            }

        });
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBSToolbar.inflateMenu(R.menu.bottom_sheet);

        mBSRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        tbSaveButton = mBSToolbar.findViewById(R.id.act_botsheet_save);
        tbSaveButton.setAlpha(0.25f);
        tbSaveButton.setOnClickListener(this::onBottomSheetSaveButtonPressed);

        tbCloseButton = mBSToolbar.getMenu().findItem(R.id.act_botsheet_close);
        tbCloseButton.setOnMenuItemClickListener(this::onBottomSheetCloseButtonPressed);

        tbDeleteButton = mBSToolbar.getMenu().findItem(R.id.act_botsheet_delete);
        tbDeleteButton.setOnMenuItemClickListener(this::onBottomSheetDeleteButtonPressed);

    }

    @SuppressWarnings("unused")
    void onBottomSheetSaveButtonPressed(View view) {
        ButterKnife.apply(damageCaseBottomSheetInputFields, REMOVE_ERRORS);

        try {
            if(damageCaseHandler.getValue() != null){
                long id = damageCaseHandler.getValue()
                        .setNameDamageCase(getFieldValueIfNotEmpty(mBSEditTextInputTitle))
                        .setAreaCode(getFieldValueIfNotEmpty(mBSEditTextInputLocation))
                        .setNamePolicyholder(getFieldValueIfNotEmpty(mBSEditTextInputPolicyholder))
                        .setNameExpert(getFieldValueIfNotEmpty(mBSEditTextInputExpert))
                        .setDate(damageCaseDate)
                        .setAreaSize(sopraMap.getArea())
                        .setCoordinates(sopraMap.getActivePoints())
                        .save();

                closeBottomSheet();
                damageCaseHandler.loadFromDatabase(id);

                Toast.makeText(getContext(), "Saved with ID:" + id, Toast.LENGTH_SHORT).show();
            }
        } catch (EditFieldValueIsEmptyException e) {
            e.showError();
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private boolean onBottomSheetCloseButtonPressed(MenuItem menuItem) {
        boolean isImportantChanged = true;

        if (damageCaseHandler.getValue() != null &&
                damageCaseHandler.getValue().isChanged()) {
            showAlert(AlertType.CLOSE);
            // Close Action will be handled in alert method
        } else {
            closeBottomSheet();
        }
        return true;
    }

    private boolean onBottomSheetDeleteButtonPressed(MenuItem menuItem) {
        showAlert(AlertType.DELETE);
        return true;
    }

    private void onBottomSheetIsHidden(View bottomSheetContainer) {

        mBSRecyclerView.setAdapter(null);
        bottomSheetExpandHandler = null;
        tbSaveButton.setAlpha(0.25f);
        mBottomSheetBehavior.allowUserSwipe(false);

        ButterKnife.apply(damageCaseBottomSheetInputFields, REMOVE_TEXT);
        ButterKnife.apply(damageCaseBottomSheetToolbarUserInserted, REMOVE_TEXT);
        ButterKnife.apply(damageCaseBottomSheetInputFields, REMOVE_ERRORS);
        ButterKnife.apply(damageCaseBottomSheetToolbarFields, REMOVE_VISIBILITY);
    }

    @SuppressWarnings("unused")
    private void onBottomSheetCollapsed(View bottomSheetContainer) {

    }

    @Override
    public BackButtonProceedPolicy onBackPressed() {

        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            showAlert(AlertType.CLOSE);
            return BackButtonProceedPolicy.SKIP_ACTIVITY;
        }

        return BackButtonProceedPolicy.WITH_ACTIVITY;
    }

    /**
     * Copyright Alexander Keck
     */
    private String getFieldValueIfNotEmpty(EditText editText) throws EditFieldValueIsEmptyException {
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
    }

    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    private void mMapFabPlusAction() {
        LocationCallbackListener lcl = new OnAddButtonLocationCallback(getContext(), callbackDone);

        if (callbackDone.get()) {
            callbackDone.set(false);
            gpsService.singleLocationCallback(lcl, 10000);
        }
    }

    private void openNewDamageCase() {

        try {
            damageCaseHandler.createNewDamageCase();
        } catch (UserManager.NoUserException e) {
            e.printStackTrace();
        }

        mBSEditDate = DateTime.now();
        mBSEditTextInputDate.setText(mBSEditDate.toString(simpleDateFormatPattern));
        openDamageCase();

    }

    private void openDamageCase() {
        /* open bottom sheet for testing purposes, will be moved to another file? TODO <-*/
        mBSContainer.setNestedScrollingEnabled(false);

        // set state 1st time
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // lock hide mode
        mBottomSheetBehavior.setHideable(false);

        // set new adapter
        bottomSheetListAdapter = new BottomSheetListAdapter(0);
        bottomSheetListAdapter.notifyDataSetChanged();
        mBSRecyclerView.swapAdapter(bottomSheetListAdapter, false);

        // Add listener to recycler view: disable button if less than 3 elements are there
        bottomSheetListAdapter.setOnItemCountChanged(newItemCount ->
                bottomSheetExpandHandler.handleNewAmount(newItemCount)
        );

        // measure height of toolbar and recycler view
        mBSToolbar.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mBSRecyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mBottomSheetBehavior.setPeekHeight(mBSToolbar.getMeasuredHeight() + mBSRecyclerView.getMeasuredHeight());

        bottomSheetExpandHandler = new BottomSheetExpandHandler();

        // set state 2nd time
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void closeBottomSheet(){
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void showAlert(AlertType alertType) {

        String title = "";
        String hint = "";
        DialogInterface.OnClickListener positiveAction = null;
        DialogInterface.OnClickListener negativeAction = null;

        if (alertType == AlertType.CLOSE) {
            title = strBSDialogCloseTitle;
            hint = strBSDialogCloseText;
            positiveAction = (dialog, id) -> {
                closeBottomSheet();
            };
            negativeAction = (dialog, id) -> {

            };


        } else if (alertType == AlertType.DELETE) {
            title = strBSDialogDeleteTitle;
            hint = strBSDialogDeleteText;
            positiveAction = (dialog, id) -> {
                Toast.makeText(getContext(), "DEL", Toast.LENGTH_SHORT).show();
                mBottomSheetBehavior.setHideable(true);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                damageCaseHandler.deleteCurrent();
            };
            negativeAction = (dialog, id) -> {
                Toast.makeText(getContext(), "NOTDEL", Toast.LENGTH_SHORT).show();
            };
        }

        new FixedDialog(getContext())
                .setTitle(title)
                .setMessage(hint)
                .setCancelable(false)
                .setPositiveButton(strBSDialogCloseOk, positiveAction)
                .setNegativeButton(strBSDialogCloseCancel, negativeAction)
                .create()
                .show();
    }

    @OnClick({R.id.bottom_sheet_input_title,
            R.id.bottom_sheet_input_location,
            R.id.bottom_sheet_input_policyholder,
            R.id.bottom_sheet_input_expert,
            R.id.bottom_sheet_input_date})
    void onClickBottomSheetDamageCaseInput(EditText editText) {

        if (editText.equals(mBSEditTextInputDate)) {
            new DatePickerDialog(
                    getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        mBSEditDate = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
                        mBSEditTextInputDate.setText(mBSEditDate.toString(simpleDateFormatPattern));
                    },
                    mBSEditDate.getYear(),
                    mBSEditDate.getMonthOfYear(),
                    mBSEditDate.getDayOfMonth())
                    .show();
            return;
        }

        String title = "";
        String hint = "";
        DialogInterface.OnClickListener positiveAction = null;
        DialogInterface.OnClickListener negativeAction = null;

        if (editText.equals(mBSEditTextInputTitle)) {
            title = strBSDialogName;
            hint = strBSDialogNameHint;
            positiveAction = (dialogInterface, i) -> {
                mBSTextViewTitle.setVisibility(View.VISIBLE);
                mBSTextViewTitleValue.setVisibility(View.VISIBLE);
                mBSTextViewTitleValue.setText(mBSEditTextInputTitle.getText());
                if(damageCaseHandler.hasValue())
                    damageCaseHandler.getValue().setNameDamageCase(mBSTextViewTitle.getText().toString());
            };
        } else if (editText.equals(mBSEditTextInputLocation)) {
            title = strBSDialogDCLocation;
            hint = strBSDialogDCLocationHint;

            positiveAction = (dialogInterface, i) -> {
                if(damageCaseHandler.hasValue())
                    damageCaseHandler.getValue().setAreaCode(mBSEditTextInputLocation.getText().toString());
            };
        } else if (editText.equals(mBSEditTextInputPolicyholder)) {
            title = strBSDialogDCPolicyholder;
            hint = strBSDialogDCPolicyholderHint;

            positiveAction = (dialogInterface, i) -> {
                if(damageCaseHandler.hasValue())
                    damageCaseHandler.getValue().setNamePolicyholder(mBSEditTextInputPolicyholder.getText().toString());
            };
        } else if (editText.equals(mBSEditTextInputExpert)) {
            title = strBSDialogDCExpert;
            hint = strBSDialogDCExpertHint;

            positiveAction = (dialogInterface, i) -> {
                if(damageCaseHandler.hasValue())
                    damageCaseHandler.getValue().setNameExpert(mBSEditTextInputExpert.getText().toString());
            };
        }

        InputRetriever.of(editText)
                .withTitle(title)
                .withHint(hint)
                .setPositiveButtonAction(positiveAction)
                .setNegativeButtonAction(negativeAction)
                .onClick(editText);
    }

    @OnClick(R.id.map_fab_plus)
    void handelActionButtonPlusClick(FloatingActionButton floatingActionButton){

        if (gpsService.wasLocationDisabled()) {

            // prompt enable location
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            getActivity().runOnUiThread(() -> Toast
                    .makeText(getContext(), strPromptEnableLocation, Toast.LENGTH_LONG)
                    .show()
            );

            return;
        }

        mMapFabPlusAction();

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            openNewDamageCase();

        } else {
            // add next point
            bottomSheetListAdapter.add();

            // scroll to last added item
            mBSRecyclerView.smoothScrollToPosition(bottomSheetListAdapter.getItemCount() - 1);
        }
    }

    @OnClick(R.id.map_fab_locate)
    void handelAnctionButtonLocateClick(FloatingActionButton floatingActionButton) {
        if (gpsService.wasLocationDisabled()) {
            mMapFabLocate.setClickable(false);
            mMapFabLocate.setImageDrawable(currentLocationUnknownDrawable);
            return;
        }
        sopraMap.mapCameraMoveToUser();
    }

    @Override
    public void onLocationFound(Location location) {
        mMapFabLocate.setClickable(true);
        mMapFabLocate.setImageDrawable(currentLocationKnownDrawable);

        if (sopraMap == null) return;

        sopraMap.drawUserPositionIndicator(location);
    }

    @Override
    public void onLocationNotFound() {
        mMapFabLocate.setClickable(false);
        mMapFabLocate.setImageDrawable(currentLocationUnknownDrawable);

        if (sopraMap == null) return;

        sopraMap.removeUserPositionIndicator();
    }

    @Override
    public void onStart() {
        super.onStart();

        // start gps
        gpsService.startGps();
        isGpsServiceBound = true;

        gpsService.ongoingLocationCallback(this);
    }


    @Override
    public void onStop() {
        super.onStop();

        if (sopraMap != null) {
            sopraMap.unregisterEvents();
        }

        // stop gps
        if (isGpsServiceBound) {
            gpsService.stopGps();
            isGpsServiceBound = false;
        }

        gpsService.stopCallback();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    private enum AlertType {
        CLOSE, DELETE
    }

    private class BottomSheetExpandHandler {
        boolean animationShown = false;

        void handleNewAmount(int newAmount) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            boolean enabled = newAmount > 2;

            tbSaveButton.setAlpha(enabled ? 1 : 0.25f);
            tbSaveButton.setEnabled(enabled);
            mBottomSheetBehavior.allowUserSwipe(enabled);

            mBSTextViewArea.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
            mBSTextViewAreaValue.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
            mBSTextViewAreaValue.setText(String.valueOf(newAmount)); // TODO! Update area

            if (enabled && !animationShown) {
                mBSContainer.animate().setInterpolator(new AccelerateInterpolator())
                        .translationY(-100);
                new Handler().postDelayed(() -> mBSContainer.animate().setInterpolator(new AccelerateInterpolator())
                        .translationY(-0), 300);
                animationShown = !animationShown;
            }
        }
    }
}
