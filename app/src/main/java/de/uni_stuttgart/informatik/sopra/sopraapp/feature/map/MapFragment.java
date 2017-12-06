package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.TEST_POLYGON_COORDINATES;
import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.TEST_POLYGON_DAMAGE;

public class MapFragment extends DaggerFragment implements FragmentBackPressed {

    // TODO: cover case of lost ACCESS_FINE_LOCATION permissions during runtime
    // TODO: replace remaining onClickListeners with ButterKnife annotations

    @Inject
    GpsService gpsService;

    View mRootView;

    /* Knife-N'-Butter section!' */

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

    @BindString(R.string.map_frag_botsheet_alert_title)
    String strBSDialogCloseTitle;

    @BindString(R.string.map_frag_botsheet_alert_text)
    String strBSDialogCloseText;

    @BindString(R.string.prompt_enable_localization)
    String strPromptEnableLocation;

    @BindString(R.string.map_frag_botsheet_alert_yes)
    String strBSDialogCloseOk;

    @BindString(R.string.map_frag_botsheet_alert_no)
    String strBSDialogCloseCancel;

    /**
     * The adapter for the horizontal recycler view
     */
    BottomSheetListAdapter bottomSheetListAdapter;
    ActionMenuItemView tbCloseButton;
    ActionMenuItemView tbSaveButton;

    private BottomSheetExpandHandler bottomSheetExpandHandler;

    private SopraMap sopraMap;

    private boolean waitingForResponse;
    private boolean isGpsServiceBound;

    /**
     * The provided bottom sheet behaviour object
     */
    private LockableBottomSheetBehaviour mBottomSheetBehavior;

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

        return mRootView;
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

            sopraMap.drawPolygonOf(TEST_POLYGON_COORDINATES, PolygonType.INSURANCE_COVERAGE, "1");
            sopraMap.drawPolygonOf(TEST_POLYGON_DAMAGE, PolygonType.DAMAGE_CASE, "2");
            sopraMap.mapCameraJump(TEST_POLYGON_COORDINATES);

        });
    }

    /**
     * Sets up bottom sheet
     */
    private void initBottomSheet() {

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheetContainer, int newState) {
                MainActivity activity = (MainActivity) getActivity();

                boolean navigationDrawerEnabled = false;


                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        navigationDrawerEnabled = true;
                        mBSRecyclerView.setAdapter(null);
                        mBSEditTextInputTitle.setText("");
                        mBSEditTextInputPolicyholder.setText("");
                        mBSEditTextInputExpert.setText("");
                        mBSEditTextInputDate.setText("");
                        bottomSheetExpandHandler = null;
                        tbSaveButton.setAlpha(0.25f);
                        mBottomSheetBehavior.allowUserSwipe(false);
                        removeErrorsFromTextFromEditTextFields();
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:

                        break;
                }


                activity.setDrawerEnabled(navigationDrawerEnabled);

            }

            @Override
            public void onSlide(@NonNull View bottomSheetContainer, float slideOffset) {
            }

        });
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBSContainer.setNestedScrollingEnabled(false);
        mBSToolbar.inflateMenu(R.menu.bottom_sheet);
        mBSRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        mBSEditTextInputTitle.setOnClickListener(InputRetriever.of(mBSEditTextInputTitle)
                .withTitle(strBSDialogName)
                .withHint(strBSDialogNameHint));

        mBSEditTextInputLocation.setOnClickListener(InputRetriever.of(mBSEditTextInputLocation)
                .withTitle(strBSDialogDCLocation)
                .withHint(strBSDialogDCLocationHint));

        mBSEditTextInputPolicyholder.setOnClickListener(InputRetriever.of(mBSEditTextInputPolicyholder)
                .withTitle(strBSDialogDCPolicyholder)
                .withHint(strBSDialogDCPolicyholderHint));

        mBSEditTextInputExpert.setOnClickListener(InputRetriever.of(mBSEditTextInputExpert)
                .withTitle(strBSDialogDCExpert)
                .withHint(strBSDialogDCExpertHint));

        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                mBSEditTextInputDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        mBSEditTextInputDate.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // init toolbar close button
        tbCloseButton = mBSToolbar.findViewById(R.id.act_botsheet_close);
        tbCloseButton.setOnClickListener(v -> {
            boolean isImportantChanged = true;

            if (isImportantChanged) {
                showCloseAlert();

            } else {

                mBottomSheetBehavior.setHideable(true);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

        tbSaveButton = mBSToolbar.findViewById(R.id.act_botsheet_save);
        tbSaveButton.setAlpha(0.25f);
        tbSaveButton.setOnClickListener(v -> {

            removeErrorsFromTextFromEditTextFields();

            try {
                String titleString = getFieldValueIfNotEmpty(mBSEditTextInputTitle);
                String locationString = getFieldValueIfNotEmpty(mBSEditTextInputLocation);
                String policyholderString = getFieldValueIfNotEmpty(mBSEditTextInputPolicyholder);
                String exportString = getFieldValueIfNotEmpty(mBSEditTextInputExpert);
                String dateString = getFieldValueIfNotEmpty(mBSEditTextInputDate);

            } catch (EditFieldValueIsEmptyException e) {
                e.showError();
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapFabPlus.setOnClickListener(v -> {

            /* open bottom sheet for testing purposes, will be moved to another file? TODO <-*/
            loadDamageCaseBottomSheet(null);

            /* GPS/Map-related section */

            if (gpsService.wasLocationDisabled()) {
                promptEnableLocation();
                return;
            }

            if (waitingForResponse) return;

            Context context = getContext();

            LocationCallbackListener lcl = new LocationCallbackListener() {
                @Override
                public void onLocationFound(Location location) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    Toast.makeText(context,
                            String.format("Latitude %s\nLongitude %s", lat, lng),
                            Toast.LENGTH_LONG)
                            .show();

                    waitingForResponse = false;
                }

                @Override
                public void onLocationNotFound() {
                    Toast.makeText(context,
                            "Es konnten keine Positionsdaten im Zeitrahmen von 10 Sekunden empfangen werden.",
                            Toast.LENGTH_LONG)
                            .show();
                    waitingForResponse = false;
                }
            };

            waitingForResponse = true;
            gpsService.singleLocationCallback(lcl, 10000);
        });

        mMapFabLocate.setOnClickListener(v -> {
            locateUser();
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        startGps();

        mMapFabLocate.setClickable(false);
    }

    @Override
    public void onStop() {
        super.onStop();

        stopGps();
    }

    private void startGps() {
        gpsService.startGps();
        isGpsServiceBound = true;

        gpsService.ongoingLocationCallback(new LocationCallbackListener() {
            @Override
            public void onLocationFound(Location location) {
                mMapFabLocate.setClickable(true);

                if (sopraMap == null) return;

                sopraMap.drawUserPositionIndicator(location);
            }

            @Override
            public void onLocationNotFound() {
                mMapFabLocate.setClickable(false);


                if (sopraMap == null) return;

                sopraMap.removeUserPositionIndicator();
            }
        });
    }

    private void stopGps() {
        if (isGpsServiceBound) {
            gpsService.stopGps();
            isGpsServiceBound = false;
        }

        gpsService.stopCallback();
    }

    private void locateUser() {
        if (gpsService.wasLocationDisabled()) {
            promptEnableLocation();
            return;
        }

        sopraMap.mapCameraMoveToUser();
    }

    @Override
    public BackButtonProceedPolicy onBackPressed() {

        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            showCloseAlert();
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

    private void loadDamageCaseBottomSheet(DamageCase damageCase) {

        int state = mBottomSheetBehavior.getState();
        if (state == BottomSheetBehavior.STATE_HIDDEN) {

            // set state 1st time
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // lock hide mode
            mBottomSheetBehavior.setHideable(false);

            // set new adapter
            bottomSheetListAdapter = new BottomSheetListAdapter(0);
            mBSRecyclerView.swapAdapter(bottomSheetListAdapter, false);

            // Add first location point
            bottomSheetListAdapter.add();

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
        } else {

            // add next point
            bottomSheetListAdapter.add();

            // scroll to last added item
            mBSRecyclerView.smoothScrollToPosition(bottomSheetListAdapter.getItemCount() - 1);
        }
    }

    private void promptEnableLocation() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        getActivity().runOnUiThread(() -> Toast
                .makeText(getContext(), strPromptEnableLocation, Toast.LENGTH_LONG)
                .show()
        );
    }

    private void showCloseAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle(strBSDialogCloseTitle)
                .setMessage(strBSDialogCloseText)
                .setCancelable(false)
                .setPositiveButton(strBSDialogCloseOk, (dialog, id) -> {
                    mBottomSheetBehavior.setHideable(true);
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                })
                .setNegativeButton(strBSDialogCloseCancel, (dialog, id) -> {

                })
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    private void removeErrorsFromTextFromEditTextFields() {
        mBSEditTextInputTitle.setError(null);
        mBSEditTextInputLocation.setError(null);
        mBSEditTextInputPolicyholder.setError(null);
        mBSEditTextInputExpert.setError(null);
        mBSEditTextInputDate.setError(null);
    }

    private class BottomSheetExpandHandler {
        boolean animationShown = false;

        void handleNewAmount(int newAmount) {
            boolean enabled = newAmount > 2;
            tbSaveButton.setAlpha(enabled ? 1 : 0.25f);
            tbSaveButton.setEnabled(enabled);
            mBottomSheetBehavior.allowUserSwipe(enabled);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if (enabled && !animationShown) {
                this.show(100);
                this.hide(300);
                animationShown = !animationShown;
            }
        }

        private void show(long value) {
            mBSContainer.animate().setInterpolator(new AccelerateInterpolator())
                    .translationY(-value);
        }

        private void hide(long delay) {
            new Handler().postDelayed(() -> mBSContainer.animate().setInterpolator(new AccelerateInterpolator())
                    .translationY(-0), delay);
        }
    }
}
