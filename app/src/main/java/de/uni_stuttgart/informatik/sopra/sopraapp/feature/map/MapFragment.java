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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
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
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.Helper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.CloseBottomSheetEvent;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.ForceClosedBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.VertexCreated;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.VertexSelected;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

@SuppressLint("SetTextI18n")
public class MapFragment
        extends MapBindFragment
        implements FragmentBackPressed, LocationCallbackListener {

    @Inject
    GpsService gpsService;

    // TODO: cover case of lost ACCESS_FINE_LOCATION permissions during runtime
    // TODO: replace remaining onClickListeners with ButterKnife annotations
    @Inject
    DamageCaseRepository damageCaseRepository;
    @Inject
    DamageCaseHandler damageCaseHandler;
    @Inject
    UserManager userManager;

    /* Knife-N'-Butter section!' */
    View mRootView;

    DateTime mBottomSheetDate = DateTime.now();

    /**
     * The adapter for the horizontal recycler view
     */
    BottomSheetListAdapter bottomSheetListAdapter;
    MenuItem tbCloseButton;
    MenuItem tbDeleteButton;
    ActionMenuItemView tbSaveButton;

    private SopraMap sopraMap;
    private boolean isGpsServiceBound;

    /**
     * The provided bottom sheet behaviour object
     */
    private LockableBottomSheetBehaviour mBottomSheetBehavior;
    private DateTime damageCaseDate = DateTime.now();
    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    // Subscribe ###################################################################################

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // guard clause for 2nd visit
        if (mRootView != null) return mRootView;

        mRootView = inflater.inflate(R.layout.activity_main_fragment_mapview,
                container,
                false);
        ButterKnife.bind(this, mRootView);

        // Set title of app bar
        getActivity().setTitle(strAppbarTitle);

        // create bottom sheet behaviour
        mBottomSheetBehavior = LockableBottomSheetBehaviour.from(mBottomSheetContainer);

        // init
        initMapView(savedInstanceState);
        initBottomSheet();

        onResume();

        damageCaseHandler
                .getLiveData()
                .observe(getActivity(), this::updateDamageCase);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(strAppbarTitle);

    }

    @Subscribe
    public void onVertexCreated(VertexCreated event) {
        if (bottomSheetListAdapter == null) return;

        int target = Math. max(bottomSheetListAdapter.getItemCount()-1, 0);
        mBottomSheetBubbleList.smoothScrollToPosition(target);
    }

    @Subscribe
    public void onVertexSelected(VertexSelected event) {
        mBottomSheetBubbleList.smoothScrollToPosition(event.vertexNumber);
    }

    //##############################################################################################

    @Subscribe
    public void onCloseBottomSheet(CloseBottomSheetEvent event) {
        if (gpsService == null) return;

        gpsService.stopSingleCallback();
    }

    private void updateDamageCase(DamageCase damageCase) {
        if (damageCase == null) {
            closeBottomSheet();
            return;
        }

        openDamageCase();
        setTextFromDamageCase(damageCase);

        for (LatLng latLng : damageCase.getCoordinates()) {
            bottomSheetListAdapter.add();
        }
    }

    private void setTextFromDamageCase(DamageCase damageCase) {
        String roundedArea = String.valueOf((double) Math.round(damageCase.getAreaSize() * 100d) / 100d);
        mBottomSheetToolbarViewArea.setText(roundedArea);
        mBottomSheetInputTitle.setText(damageCase.getNameDamageCase());
        mBottomSheetToolbarViewTitle.setText(damageCase.getNameDamageCase());
        mBottomSheetInputLocation.setText(damageCase.getAreaCode());
        mBottomSheetInputPolicyholder.setText(damageCase.getNamePolicyholder());
        mBottomSheetInputExpert.setText(damageCase.getNameExpert());
        mBottomSheetInputDate.setText(damageCase.getDate().toString(strSimpleDateFormatPattern));
        mBottomSheetToolbarViewDate.setText(damageCase.getDate().toString(strSimpleDateFormatPattern));
        mBottomSheetDate = damageCase.getDate();
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
                    mBottomSheetToolbarViewArea.setText("" + (double) Math.round(area * 100d) / 100d)
            );

        });

    }

    //Button #######################################################################################

    private void initBottomSheet() {

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheetContainer, int newState) {
                ((MainActivity) getActivity()).setDrawerEnabled(newState == BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onSlide(@NonNull View bottomSheetContainer, float slideOffset) {

            }
        });

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetToolbar.inflateMenu(R.menu.bottom_sheet);

        mBottomSheetBubbleList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        tbSaveButton = mBottomSheetToolbar.findViewById(R.id.act_botsheet_save);
        tbSaveButton.setAlpha(0.25f);
        tbSaveButton.setOnClickListener(this::onBottomSheetSaveButtonPressed);

        tbCloseButton = mBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_close);
        tbCloseButton.setOnMenuItemClickListener(this::onBottomSheetCloseButtonPressed);

        tbDeleteButton = mBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_delete);
        tbDeleteButton.setOnMenuItemClickListener(this::onBottomSheetDeleteButtonPressed);

    }

    void onBottomSheetSaveButtonPressed(View view) {
        ButterKnife.apply(mBottomSheetInputs, REMOVE_ERRORS);

        try {
            if (damageCaseHandler.getValue() != null) {
                long id = damageCaseHandler.getValue()
                        .setNameDamageCase(getIfNotEmptyElseThrow(mBottomSheetInputTitle))
                        .setAreaCode(getIfNotEmptyElseThrow(mBottomSheetInputLocation))
                        .setNamePolicyholder(getIfNotEmptyElseThrow(mBottomSheetInputPolicyholder))
                        .setNameExpert(getIfNotEmptyElseThrow(mBottomSheetInputExpert))
                        .setDate(damageCaseDate)
                        .setAreaSize(sopraMap.getArea())
                        .setCoordinates(sopraMap.getActivePoints())
                        .save();

                closeBottomSheet();

            }
        } catch (EditFieldValueIsEmptyException e) {
            e.showError();
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean onBottomSheetCloseButtonPressed(MenuItem menuItem) {
        showCloseAlertIfChanged();
        return true;
    }

    private boolean onBottomSheetDeleteButtonPressed(MenuItem menuItem) {
        showDeleteAlert();
        return true;
    }

    private void addVertexToActivePolygon() {
        LocationCallbackListener lcl = new OnAddButtonLocationCallback(getContext(), callbackDone);

        if (callbackDone.get()) {
            callbackDone.set(false);
            gpsService.singleLocationCallback(lcl, 10000);
        }

        // mock locations for testing
//        Handler handler = new Handler();
//        handler.postDelayed(() -> EventBus.getDefault().post(new VertexCreated(Helper.getRandomLatLng())), 500);
    }

    private void openNewDamageCase() {

        try {
            damageCaseHandler.createNewDamageCase();
        } catch (UserManager.NoUserException e) {
            e.printStackTrace();
        }

        mBottomSheetDate = DateTime.now();
        mBottomSheetInputDate.setText(mBottomSheetDate.toString(strSimpleDateFormatPattern));
        openDamageCase();

    }

    private void openDamageCase() {
        /* open bottom sheet for testing purposes, will be moved to another file? TODO <-*/
        mBottomSheetContainer.setNestedScrollingEnabled(false);

        // set state 1st time
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // lock hide mode
        mBottomSheetBehavior.setHideable(false);

        if (bottomSheetListAdapter != null)
            getLifecycle().removeObserver(bottomSheetListAdapter);

        // set new adapter
        bottomSheetListAdapter = new BottomSheetListAdapter(0);
        getLifecycle().addObserver(bottomSheetListAdapter);
        bottomSheetListAdapter.notifyDataSetChanged();
        mBottomSheetBubbleList.swapAdapter(bottomSheetListAdapter, false);

        // Add listener to recycler view: disable button if less than 3 elements are there
        bottomSheetListAdapter.setOnItemCountChanged(new BottomSheetExpandHandler());

        // set state 2nd time
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void closeBottomSheet() {
        resetBottomSheetContent();

        if (gpsService != null)
            gpsService.stopSingleCallback();

        EventBus.getDefault().post(new CloseBottomSheetEvent());
    }

    private void resetBottomSheetContent() {
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        tbSaveButton.setAlpha(0.25f);
        mBottomSheetBehavior.allowUserSwipe(false);

        ButterKnife.apply(mBottomSheetInputs, REMOVE_TEXT);
        ButterKnife.apply(mBottomSheetInputs, REMOVE_ERRORS);

    }

    private void showCloseAlertIfChanged() {
        if ((damageCaseHandler.getValue() != null && damageCaseHandler.getValue().isChanged())) {
            showCloseAlert();

        } else {
            closeBottomSheet();
        }
    }

    //Alert ########################################################################################

    private void showCloseAlert() {
        new FixedDialog(getContext())
                .setTitle(strBottomSheetCloseDialogHeader)
                .setMessage(strBottomSheetCloseDialogMessage)
                .setCancelable(false)
                .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> {
                    EventBus.getDefault().post(new ForceClosedBottomSheet());
                    closeBottomSheet();
                })
                .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {
                })
                .create()
                .show();
    }

    private void showDeleteAlert() {
        new FixedDialog(getContext())
                .setTitle(strBottomSheetDeleteDialogHeader)
                .setMessage(strBottomSheetDeleteDialogMessage)
                .setCancelable(false)
                .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> damageCaseHandler.deleteCurrent())
                .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {
                })
                .create()
                .show();
    }

    @OnClick(R.id.bottom_sheet_input_date)
    void onClickBottomSheetInputDate(EditText editText) {
        Log.e("DATE", editText.getText() + "");
        new DatePickerDialog(
                getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    mBottomSheetDate = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                    mBottomSheetInputDate.setText(mBottomSheetDate.toString(strSimpleDateFormatPattern));
                    mBottomSheetToolbarViewDate.setText(mBottomSheetDate.toString(strSimpleDateFormatPattern));
                },
                mBottomSheetDate.getYear(),
                mBottomSheetDate.getMonthOfYear() - 1,
                mBottomSheetDate.getDayOfMonth()
        ).show();
    }

    //Handle Input Retriever #######################################################################

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_title)
    void onClickBottomSheetInputTitle(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogTitleHeader)
                .withHint(strBottomSheetInpDialogTitleHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    mBottomSheetToolbarViewTitle.setText(mBottomSheetInputTitle.getText());
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setNameDamageCase(mBottomSheetInputTitle.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_location)
    void onClickBottomSheetInputLocation(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogLocationHeader)
                .withHint(strBottomSheetInpDialogLocationHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setAreaCode(mBottomSheetInputLocation.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_policyholder)
    void onClickBottomSheetInputPolicyHolder(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogPolicyholderHeader)
                .withHint(strBottomSheetInpDialogPolicyholderHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setNamePolicyholder(mBottomSheetInputPolicyholder.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_expert)
    void onClickBottomSheetInputExpert(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogExpertHeader)
                .withHint(strBottomSheetInpDialogExpertHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setNameExpert(mBottomSheetInputExpert.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();
    }

    @OnClick(R.id.fab_plus)
    void handelFloatingActionButtonPlusClick(FloatingActionButton floatingActionButton) {
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

        addVertexToActivePolygon();

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            openNewDamageCase();
        }
    }

    //##############################################################################################

    @OnClick(R.id.fab_locate)
    void handelFloatingActionButtonLocateClick(FloatingActionButton floatingActionButton) {
        if (gpsService.wasLocationDisabled()) {
            mFabLocate.setClickable(false);
            mFabLocate.setImageDrawable(currentLocationUnknownDrawable);
            return;
        }

        sopraMap.mapCameraMoveToUser();
    }

    @Override
    public void onLocationFound(Location location) {
        mFabLocate.setClickable(true);
        mFabLocate.setImageDrawable(currentLocationKnownDrawable);

        if (sopraMap == null) return;

        sopraMap.drawUserPositionIndicator(location);
    }

    @Override
    public void onLocationNotFound() {
        mFabLocate.setClickable(false);
        mFabLocate.setImageDrawable(currentLocationUnknownDrawable);

        if (sopraMap == null) return;

        sopraMap.removeUserPositionIndicator();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        // start gps
        gpsService.startGps();
        isGpsServiceBound = true;

        gpsService.ongoingLocationCallback(this);
    }

    //Lifecycle ####################################################################################

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        // stop gps
        if (isGpsServiceBound) {
            gpsService.stopGps();
            isGpsServiceBound = false;
        }

        gpsService.stopAllCallbacks();
        bottomSheetListAdapter = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public BackButtonProceedPolicy onBackPressed() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            showCloseAlertIfChanged();
            return BackButtonProceedPolicy.SKIP_ACTIVITY;
        }
        return BackButtonProceedPolicy.WITH_ACTIVITY;
    }

    //Override #####################################################################################

    private String getIfNotEmptyElseThrow(EditText editText) throws EditFieldValueIsEmptyException {
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
    }

    //##############################################################################################

    private class BottomSheetHere {

        BottomSheetHere(View view) {
            ButterKnife.bind(this, view);
        }

    }

    //##############################################################################################

    private class BottomSheetExpandHandler implements BottomSheetListAdapter.ItemCountListener {
        boolean animationShown = false;

        void handleNewAmount(int newAmount) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            boolean enabled = newAmount > 2;

            tbSaveButton.setAlpha(enabled ? 1 : 0.25f);
            tbSaveButton.setEnabled(enabled);
            mBottomSheetBehavior.allowUserSwipe(enabled);

            if (enabled && !animationShown) {
                mBottomSheetContainer
                        .animate()
                        .setInterpolator(new AccelerateInterpolator())
                        .translationY(-100);
                new Handler().postDelayed(() -> mBottomSheetContainer.animate().setInterpolator(new AccelerateInterpolator())
                        .translationY(-0), 300);
                animationShown = !animationShown;
            }
        }

        @Override
        public void onItemCountChanged(int newItemCount) {
            handleNewAmount(newItemCount);
        }
    }
}
