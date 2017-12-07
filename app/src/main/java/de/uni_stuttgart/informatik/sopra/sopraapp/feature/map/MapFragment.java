package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
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
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.CloseBottomSheetEvent;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.VertexCreated;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.VertexSelected;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.TEST_POLYGON_COORDINATES;

@SuppressLint("SetTextI18n")
public class MapFragment
        extends MapBindFragment
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

    // Subscribe ###################################################################################
    @Subscribe
    void onVertexCreated(VertexCreated event){
        mBSRecyclerView.smoothScrollToPosition(bottomSheetListAdapter.getItemCount() - 1);
    }

    @Subscribe
    void onVertexSelected(VertexSelected event){
        mBSRecyclerView.smoothScrollToPosition(event.vertexNumber);
    }

    @Subscribe
    void onCloseBottomSheet(CloseBottomSheetEvent event) {
        if (gpsService == null) return;

        gpsService.stopCallback();
    }

    //##############################################################################################

    private void updateDamageCase(DamageCase damageCase){
        if(damageCase == null){
            closeBottomSheet();
            return;
        }

        openDamageCase();
        setTextFromDamageCase(damageCase);

        for (LatLng latLng : damageCase.getCoordinates()) {
            bottomSheetListAdapter.add();
        }
    }


    private void setTextFromDamageCase(DamageCase damageCase){
        mBSTextViewAreaValue.setText(damageCase.getAreaSize() + "");
        mBSEditTextInputTitle.setText(damageCase.getNameDamageCase());
        mBSTextViewTitleValue.setText(damageCase.getNameDamageCase());
        mBSEditTextInputLocation.setText(damageCase.getAreaCode());
        mBSEditTextInputPolicyholder.setText(damageCase.getNamePolicyholder());
        mBSEditTextInputExpert.setText(damageCase.getNameExpert());
        mBSEditTextInputDate.setText(damageCase.getDate().toString(simpleDateFormatPattern));
        mBSTextViewDateValue.setText(damageCase.getDate().toString(simpleDateFormatPattern));
        mBSEditDate = damageCase.getDate();
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
            getLifecycle().addObserver(sopraMap);

            sopraMap.areaLiveData().observe(this, area ->
                    mBSTextViewAreaValue.setText("" + (double)Math.round(area * 100d) / 100d)
            );

            //sopraMap.drawPolygonOf(TEST_POLYGON_COORDINATES, PolygonType.INSURANCE_COVERAGE, "1");
            sopraMap.mapCameraJump(TEST_POLYGON_COORDINATES);

        });

    }

    private void initBottomSheet() {

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheetContainer, int newState) {

                ((MainActivity) getActivity()).setDrawerEnabled(newState == BottomSheetBehavior.STATE_HIDDEN);

                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    mBSRecyclerView.setAdapter(null);
                    bottomSheetExpandHandler = null;
                    tbSaveButton.setAlpha(0.25f);
                    mBottomSheetBehavior.allowUserSwipe(false);

                    ButterKnife.apply(damageCaseBottomSheetInputFields, REMOVE_TEXT);
                    ButterKnife.apply(damageCaseBottomSheetInputFields, REMOVE_ERRORS);
                }
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

    //Button #######################################################################################

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
        showCloseAlertIfChanged();
        return true;
    }

    private boolean onBottomSheetDeleteButtonPressed(MenuItem menuItem) {
        showDeleteAlert();
        return true;
    }


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

        if(bottomSheetListAdapter != null)
            getLifecycle().removeObserver(bottomSheetListAdapter);
        // set new adapter
        bottomSheetListAdapter = new BottomSheetListAdapter(0);
        getLifecycle().addObserver(bottomSheetListAdapter);
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
        EventBus.getDefault().post(new CloseBottomSheetEvent());
    }

    //Alert ########################################################################################

    private void showCloseAlertIfChanged(){
        if (damageCaseHandler.getValue() != null && damageCaseHandler.getValue().isChanged())
            showCloseAlert();
        else closeBottomSheet();
    }

    private void showCloseAlert(){
        new FixedDialog(getContext())
                .setTitle(strBSDialogCloseTitle)
                .setMessage(strBSDialogCloseText)
                .setCancelable(false)
                .setPositiveButton(strBSDialogCloseOk, (dialog, id) -> closeBottomSheet())
                .setNegativeButton(strBSDialogCloseCancel, (dialog, id) -> {})
                .create()
                .show();
    }

    private void showDeleteAlert(){
        new FixedDialog(getContext())
                .setTitle(strBSDialogDeleteTitle)
                .setMessage(strBSDialogDeleteText)
                .setCancelable(false)
                .setPositiveButton(strBSDialogCloseOk, (dialog, id) -> damageCaseHandler.deleteCurrent())
                .setNegativeButton(strBSDialogCloseCancel, (dialog, id) -> {})
                .create()
                .show();
    }

    //Handle Input Retriever #######################################################################

    @OnClick(R.id.bottom_sheet_input_date)
    void onClickBottomSheetInputDate(EditText editText){
        new DatePickerDialog(
            getContext(),
            (view, year, monthOfYear, dayOfMonth) -> {
                mBSEditDate = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
                mBSEditTextInputDate.setText(mBSEditDate.toString(simpleDateFormatPattern));
                mBSTextViewDateValue.setText(mBSEditDate.toString(simpleDateFormatPattern));
            },
            mBSEditDate.getYear(),
            mBSEditDate.getMonthOfYear(),
            mBSEditDate.getDayOfMonth()
        ).show();
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_title)
    void onClickBottomSheetInputTitle(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBSDialogName)
                .withHint(strBSDialogNameHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    mBSTextViewTitleValue.setText(mBSEditTextInputTitle.getText());
                    if(damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setNameDamageCase(mBSEditTextInputTitle.getText().toString());
                })
                .setNegativeButtonAction(null)
                .onClick(editText);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_location)
    void onClickBottomSheetInputLocation(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBSDialogDCLocation)
                .withHint(strBSDialogDCLocationHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if(damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setAreaCode(mBSEditTextInputLocation.getText().toString());
                })
                .setNegativeButtonAction(null)
                .onClick(editText);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_policyholder)
    void onClickBottomSheetInputPolicyHolder(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBSDialogDCPolicyholder)
                .withHint(strBSDialogDCPolicyholderHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if(damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setNamePolicyholder(mBSEditTextInputPolicyholder.getText().toString());
                })
                .setNegativeButtonAction(null)
                .onClick(editText);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_expert)
    void onClickBottomSheetInputExpert(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBSDialogDCExpert)
                .withHint(strBSDialogDCExpertHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if(damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setNameExpert(mBSEditTextInputExpert.getText().toString());
                })
                .setNegativeButtonAction(null)
                .onClick(editText);
    }

    //##############################################################################################


    @OnClick(R.id.map_fab_plus)
    void handelActionButtonPlusClick(FloatingActionButton floatingActionButton){
        if (gpsService.wasLocationDisabled()) {
            // prompt enable location
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            getActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), strPromptEnableLocation, Toast.LENGTH_LONG).show()
            );

            return;
        }

        mMapFabPlusAction();

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            openNewDamageCase();
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

    //Lifecycle ####################################################################################

    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        // start gps
        gpsService.startGps();
        isGpsServiceBound = true;

        gpsService.ongoingLocationCallback(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

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
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    //Override #####################################################################################

    @Override
    public BackButtonProceedPolicy onBackPressed() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            showCloseAlertIfChanged();
            return BackButtonProceedPolicy.SKIP_ACTIVITY;
        }
        return BackButtonProceedPolicy.WITH_ACTIVITY;
    }

    //##############################################################################################

    private class BottomSheetExpandHandler {
        boolean animationShown = false;

        void handleNewAmount(int newAmount) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            boolean enabled = newAmount > 0;

            tbSaveButton.setAlpha(enabled ? 1 : 0.25f);
            tbSaveButton.setEnabled(enabled);
            mBottomSheetBehavior.allowUserSwipe(enabled);

            if (enabled && !animationShown) {
                mBSContainer
                        .animate()
                        .setInterpolator(new AccelerateInterpolator())
                        .translationY(-100);
                new Handler().postDelayed(() -> mBSContainer.animate().setInterpolator(new AccelerateInterpolator())
                        .translationY(-0), 300);
                animationShown = !animationShown;
            }
        }
    }
}
