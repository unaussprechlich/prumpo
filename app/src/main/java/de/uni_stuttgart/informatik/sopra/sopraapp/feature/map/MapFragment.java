package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
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

    private static boolean createdOnce;
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
    View mRootView;
    Observer damaageCaseObserver;
    private BottomSheetMapBehaviour bottomSheetMapBehaviour;
    private SopraMap sopraMap;
    private boolean isGpsServiceBound;
    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("OnCreateView", "init");
        mRootView = inflater.inflate(R.layout.activity_main_fragment_mapview,
                container,
                false);
        ButterKnife.bind(this, mRootView);

        bottomSheetMapBehaviour = new BottomSheetMapBehaviour(LockableBottomSheetBehaviour.from(mBottomSheetContainer));

        damaageCaseObserver = o -> {
            new Handler().postDelayed(() -> bottomSheetMapBehaviour.updateDamageCase((DamageCase) o), 300);

        };

        // if (!createdOnce)
            initMapView(savedInstanceState);

        onResume();
        damageCaseHandler.getLiveData()
                .observe(getActivity(), damaageCaseObserver);


        createdOnce = true;


        return mRootView;
    }

    @Override
    public void onLocationFound(Location location) {
        Log.i("onLocationFound", "init");
        mFabLocate.setClickable(true);
        mFabLocate.setImageDrawable(currentLocationKnownDrawable);

        if (sopraMap == null) return;

        sopraMap.drawUserPositionIndicator(location);
    }

    @Override
    public void onLocationNotFound() {
        Log.i("onLocationNotFound", "init");
        mFabLocate.setClickable(false);
        mFabLocate.setImageDrawable(currentLocationUnknownDrawable);

        if (sopraMap == null) return;

        sopraMap.removeUserPositionIndicator();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i("onViewCreated", "init");
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(strAppbarTitle);

    }

    private void initMapView(Bundle savedInstanceState) {
        Log.i("initMapView", "init");
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

    @OnClick(R.id.fab_plus)
    void handelFloatingActionButtonPlusClick(FloatingActionButton floatingActionButton) {
        Log.i("handlefaButtonPlusClick", "init");
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

        if (bottomSheetMapBehaviour.lockableBottomSheetBehaviour.getState()
                == BottomSheetBehavior.STATE_HIDDEN) {
            openNewDamageCase();
        }
    }

    @OnClick(R.id.fab_locate)
    void handelFloatingActionButtonLocateClick(FloatingActionButton floatingActionButton) {
        Log.i("handlefaLocate", "init");
        if (gpsService.wasLocationDisabled()) {
            mFabLocate.setClickable(false);
            mFabLocate.setImageDrawable(currentLocationUnknownDrawable);
            return;
        }

        sopraMap.mapCameraMoveToUser();
    }

    private void addVertexToActivePolygon() {
        Log.i("addVertexToAcPoly", "init");
        LocationCallbackListener lcl = new OnAddButtonLocationCallback(getContext(), callbackDone);

        if (callbackDone.get()) {
            callbackDone.set(false);
            gpsService.singleLocationCallback(lcl, 10000);
        }
    }


    private void openNewDamageCase() {
        Log.i("openNewDamageCase", "init");
        bottomSheetMapBehaviour.openNew();

    }

    private void openDamageCase() {
        Log.i("openDamageCase", "init");
        bottomSheetMapBehaviour.open();
    }


    @Override
    public void onStop() {
        Log.i("onStop", "init");
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        // stop gps
        if (isGpsServiceBound) {
            gpsService.stopGps();
            isGpsServiceBound = false;
        }

        gpsService.stopAllCallbacks();
        damageCaseHandler.getLiveData().removeObserver(damaageCaseObserver);
    }

    @Override
    public void onPause() {
        Log.i("onPause", "init");
        super.onPause();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        Log.i("onResume", "init");
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public BackButtonProceedPolicy onBackPressed() {
        Log.i("onBackButtonPressed", "init");
        if (bottomSheetMapBehaviour.lockableBottomSheetBehaviour.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetMapBehaviour.showCloseAlertIfChanged();
            return BackButtonProceedPolicy.SKIP_ACTIVITY;
        }
        return BackButtonProceedPolicy.WITH_ACTIVITY;
    }


    @Subscribe
    public void onVertexCreated(VertexCreated event) {
        Log.i("onVertexCreated", "init");
        int target = Math.max(bottomSheetMapBehaviour.bottomSheetListAdapter.getItemCount() - 1, 0);
        mBottomSheetBubbleList.smoothScrollToPosition(target);
    }

    @Subscribe
    public void onVertexSelected(VertexSelected event) {
        Log.i("onVertexSelected", "init");
        mBottomSheetBubbleList.smoothScrollToPosition(event.vertexNumber);
    }

    //##############################################################################################

    @Subscribe
    public void onCloseBottomSheet(CloseBottomSheetEvent event) {
        Log.i("onCloseButtonSheet", "init");
        if (gpsService == null) return;

        gpsService.stopSingleCallback();

        bottomSheetMapBehaviour.hide();
        new Handler().postDelayed(() -> {
            bottomSheetMapBehaviour = new BottomSheetMapBehaviour(LockableBottomSheetBehaviour.from(mBottomSheetContainer));
        }, 400);


        // bottomSheetMapBehaviour = new BottomSheetMapBehaviour(LockableBottomSheetBehaviour.from(mBottomSheetContainer));
    }

    @OnClick(R.id.bottom_sheet_input_date)
    void onClickBottomSheetInputDate(EditText editText) {
        bottomSheetMapBehaviour.onClickBottomSheetInputDate(editText);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_title)
    void onClickBottomSheetInputTitle(EditText editText) {
        bottomSheetMapBehaviour.onClickBottomSheetInputTitle(editText);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_location)
    void onClickBottomSheetInputLocation(EditText editText) {
        bottomSheetMapBehaviour.onClickBottomSheetInputLocation(editText);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_policyholder)
    void onClickBottomSheetInputPolicyHolder(EditText editText) {
        bottomSheetMapBehaviour.onClickBottomSheetInputPolicyHolder(editText);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_expert)
    void onClickBottomSheetInputExpert(EditText editText) {
        bottomSheetMapBehaviour.onClickBottomSheetInputExpert(editText);
    }

    @Override
    public void onStart() {
        Log.i("onStart", "init");
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        // start gps
        gpsService.startGps();
        isGpsServiceBound = true;

        gpsService.ongoingLocationCallback(this);
    }

    class BottomSheetMapBehaviour implements BottomSheetListAdapter.ItemCountListener {
        LockableBottomSheetBehaviour lockableBottomSheetBehaviour;
        MenuItem tbCloseButton;
        MenuItem tbDeleteButton;
        ActionMenuItemView tbSaveButton;
        DateTime mBottomSheetDate = DateTime.now();
        BottomSheetListAdapter bottomSheetListAdapter;
        boolean animationShown = false;
        private DateTime damageCaseDate = DateTime.now();

        public BottomSheetMapBehaviour(LockableBottomSheetBehaviour lockableBottomSheetBehaviour) {
            this.lockableBottomSheetBehaviour = lockableBottomSheetBehaviour;
            init();
        }

        void init() {
            Log.i("BS", "init");
            lockableBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheetContainer, int newState) {
                    ((MainActivity) getActivity()).setDrawerEnabled(newState == BottomSheetBehavior.STATE_HIDDEN);
                }

                @Override
                public void onSlide(@NonNull View bottomSheetContainer, float slideOffset) {

                }

            });

            lockableBottomSheetBehaviour.setHideable(true);
            lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);

            mBottomSheetToolbar.getMenu().clear();
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
            Log.i("BS", "onBottomSheetSaveButtonPressed");
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

                    Toast.makeText(getContext(), "Saved with ID:" + id, Toast.LENGTH_SHORT).show();

                }
            } catch (EditFieldValueIsEmptyException e) {
                e.showError();
                lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        private boolean onBottomSheetCloseButtonPressed(MenuItem menuItem) {
            Log.i("BS", "onBottomSheetCloseButtonPressed");
            showCloseAlertIfChanged();
            return true;
        }

        private boolean onBottomSheetDeleteButtonPressed(MenuItem menuItem) {
            Log.i("BS", "onBottomSheetDeletedButtonPressed");
            showDeleteAlert();
            return true;
        }

        public void open() {
            Log.i("BS", "open");

            /* open bottom sheet for testing purposes, will be moved to another file? TODO <-*/
            mBottomSheetContainer.setNestedScrollingEnabled(false);

            // set state 1st time
            lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // lock hide mode
            lockableBottomSheetBehaviour.setHideable(false);

            // if (bottomSheetListAdapter != null)
            //   getLifecycle().removeObserver(bottomSheetListAdapter);
            // TODO! Event here

            // set new adapter
            bottomSheetListAdapter = new BottomSheetListAdapter(0);
            getLifecycle().addObserver(bottomSheetListAdapter);
            bottomSheetListAdapter.notifyDataSetChanged();
            mBottomSheetBubbleList.setAdapter(bottomSheetListAdapter);

            // Add listener to recycler view: disable button if less than 3 elements are there
            bottomSheetListAdapter.setOnItemCountChanged(this);

            // set state 2nd time
            lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        @Override
        public void onItemCountChanged(int newItemCount) {
            Log.i("BS", "onItemCountChanged");
            handleNewAmount(newItemCount);
        }

        public void openNew() {
            Log.i("BS", "openNew");
            // mBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_delete).setEnabled(false);

            try {
                damageCaseHandler.createNewDamageCase();
            } catch (UserManager.NoUserException e) {
                e.printStackTrace();
            }

            bottomSheetMapBehaviour.mBottomSheetDate = DateTime.now();
            mBottomSheetInputDate.setText(bottomSheetMapBehaviour.mBottomSheetDate.toString(strSimpleDateFormatPattern));
            open();
        }

        private String getIfNotEmptyElseThrow(EditText editText) throws EditFieldValueIsEmptyException {
            String text = editText.getText().toString();
            if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
            return text;
        }

        private void closeBottomSheet() {
            Log.i("BS", "closeBottomSheet");
            if (gpsService != null)
                gpsService.stopSingleCallback();

            EventBus.getDefault().post(new CloseBottomSheetEvent());
        }

        public void hide() {
            Log.i("BS", "hide");
            lockableBottomSheetBehaviour.setHideable(true);
            lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        private void showDeleteAlert() {
            Log.i("BS", "showDeleteAlert");
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

        private void showCloseAlertIfChanged() {
            Log.i("BS", "showCloseAlertIfChanged");
            if ((damageCaseHandler.getValue() != null && damageCaseHandler.getValue().isChanged())
                    || (bottomSheetListAdapter != null && bottomSheetListAdapter.getItemCount() > 0)) {
                showCloseAlert();
            } else {
                closeBottomSheet();
            }
        }

        private void showCloseAlert() {
            Log.i("BS", "showCloseAlert");
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

        public void updateDamageCase(DamageCase damageCase) {
            Log.i("BS", "updateDamageCase");
            if (damageCase == null) {
                // closeBottomSheet();
                return;
            }

            open();

            Log.e("COORD", damageCase.getCoordinates().toString());

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

            for (LatLng latLng : damageCase.getCoordinates()) {
                bottomSheetListAdapter.add();
            }
        }

        void handleNewAmount(int newAmount) {
            Log.i("BS", "handle new Amount");
            lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            boolean enabled = newAmount > 2;

            tbSaveButton.setAlpha(enabled ? 1 : 0.25f);
            tbSaveButton.setEnabled(enabled);
            lockableBottomSheetBehaviour.allowUserSwipe(enabled);

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

        void onClickBottomSheetInputTitle(EditText editText) {
            Log.e("ERROR", "message");
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
    }


}
