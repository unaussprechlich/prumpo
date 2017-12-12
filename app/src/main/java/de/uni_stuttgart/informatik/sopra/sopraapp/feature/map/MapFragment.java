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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateInterpolator;
import android.widget.*;

import butterknife.*;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsVertex;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

@SuppressWarnings("unchecked")
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

    @BindView(R.id.bottom_sheet_container)
    NestedScrollView mBottomSheetContainer;


    /* Knife-N'-Butter section!' */
    private View mRootView;
    private BottomSheet currentBottomSheet = null;
    private LockableBottomSheetBehaviour mBottomSheetBehavior;
    private SopraMap sopraMap;
    private AtomicBoolean callbackDone = new AtomicBoolean(true);
    private Observer damageCaseObserver = damageCase -> updateDamageCase((DamageCase) damageCase);
    private boolean isGpsServiceBound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mRootView != null)
            return mRootView;

        mRootView = inflater.inflate(R.layout.activity_main_fragment_mapview,
                container,
                false);
        ButterKnife.bind(this, mRootView);
        setUpBottomSheet();
        initMapView(savedInstanceState);

        onResume();

        return mRootView;
    }

    private void updateDamageCase(DamageCase damageCase) {
        Log.e("LOG", "dc");

        if (damageCase == null || currentBottomSheet != null)
            return;

        //noinspection ConstantConditions
        currentBottomSheet = new BottomSheetDamageCase(damageCase);
        BottomSheetDamageCase bsdc = (BottomSheetDamageCase) currentBottomSheet;

        for (LatLng latLng : damageCase.getCoordinates()) {
            bsdc.bottomSheetListAdapter.add();
        }

        new Handler().postDelayed(currentBottomSheet::show, 400);

    }

    private void setUpBottomSheet() {
        mBottomSheetBehavior = LockableBottomSheetBehaviour.from(mBottomSheetContainer);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheetContainer, int newState) {
                ((MainActivity) getActivity()).setDrawerEnabled(newState == BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onSlide(@NonNull View bottomSheetContainer, float slideOffset) {

            }

        });

        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

            sopraMap.areaLiveData().observe(this, (Double area) -> {
                if ((currentBottomSheet.getType() == BottomSheet.TYPE.DAMAGE_CASE_NEW || currentBottomSheet.getType() == BottomSheet.TYPE.DAMAGE_CASE)
                        && area != null) {
                    Log.e("AREA", "AREA");
                    BottomSheetNewDamageCase bsdc = (BottomSheetNewDamageCase) currentBottomSheet;
                    bsdc.mBottomSheetToolbarViewArea.setText("" + (double) Math.round(area * 100d) / 100d);
                }
            });

        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(strAppbarTitle);
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


    @Subscribe
    public void onVertexCreated(EventsVertex.Created event) {
        Log.e("SUBS", "vertexCreated" + event.position);
        if (currentBottomSheet == null && (currentBottomSheet.getType() != BottomSheet.TYPE.DAMAGE_CASE || currentBottomSheet.getType() != BottomSheet.TYPE.DAMAGE_CASE_NEW))
            return;

        BottomSheetNewDamageCase bsdc = (BottomSheetNewDamageCase) currentBottomSheet;

        int target = Math.max(bsdc.bottomSheetListAdapter.getItemCount() - 1, 0);
        bsdc.mBottomSheetBubbleList.smoothScrollToPosition(target);
    }

    @Subscribe
    public void onVertexSelected(EventsVertex.Selected event) {
        Log.e("SUBS", "vertexSelected" + event.vertexNumber);
        if (currentBottomSheet == null || (currentBottomSheet.getType() != BottomSheet.TYPE.DAMAGE_CASE || currentBottomSheet.getType() != BottomSheet.TYPE.DAMAGE_CASE_NEW))
            return;


        BottomSheetNewDamageCase bsdc = (BottomSheetNewDamageCase) currentBottomSheet;
        bsdc.mBottomSheetBubbleList.smoothScrollToPosition(event.vertexNumber);
    }

    @Subscribe
    public void onCloseBottomSheet(EventsBottomSheet.Close event) {
        if (gpsService == null) return;

        gpsService.stopSingleCallback();
    }


    @Override
    public BackButtonProceedPolicy onBackPressed() {
        Log.i("onBackButtonPressed", "init");
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            // mBottomSheetBehavior
            return BackButtonProceedPolicy.SKIP_ACTIVITY;
        }
        return BackButtonProceedPolicy.WITH_ACTIVITY;
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

        if (currentBottomSheet == null) {
            try {
                damageCaseHandler.createNewDamageCase();
                currentBottomSheet = new BottomSheetNewDamageCase();
                currentBottomSheet.show();
            } catch (UserManager.NoUserException e) {
                e.printStackTrace();
            }

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

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        // start gps
        gpsService.startGps();
        isGpsServiceBound = true;

        gpsService.ongoingLocationCallback(this);

        damageCaseHandler
                .getLiveData()
                .observe(getActivity(), damageCaseObserver);
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
        damageCaseHandler.getLiveData().removeObserver(damageCaseObserver);
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


    interface BottomSheet extends BottomSheetListAdapter.ItemCountListener {

        enum TYPE {
            DAMAGE_CASE, DAMAGE_CASE_NEW
        }

        void show();

        void close();

        TYPE getType();

    }


    class BottomSheetNewDamageCase implements BottomSheet {

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


        MenuItem tbCloseButton;
        MenuItem tbDeleteButton;
        ActionMenuItemView tbSaveButton;
        BottomSheetListAdapter bottomSheetListAdapter;
        private boolean animationShown = false;
        private DateTime damageCaseDate = DateTime.now();
        private View thisBottomSheetView;

        public BottomSheetNewDamageCase() {
            Log.e("BOT", "NEWBOTTOM_SHEET");
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            mBottomSheetContainer.removeAllViewsInLayout();
            mBottomSheetContainer.setNestedScrollingEnabled(false);
            mBottomSheetBehavior.allowUserSwipe(false);
            thisBottomSheetView = layoutInflater.inflate(R.layout.activity_main_bottom_sheet, null, false);

            ButterKnife.bind(this, thisBottomSheetView);

            bottomSheetListAdapter = new BottomSheetListAdapter(0);
            bottomSheetListAdapter.setOnItemCountChanged(this);
            mBottomSheetBubbleList.setAdapter(bottomSheetListAdapter);
            mBottomSheetBubbleList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mBottomSheetBehavior.setPeekHeight(dimenBottomSheetPeekHeight);

            mBottomSheetToolbar.inflateMenu(R.menu.bottom_sheet);

            tbSaveButton = mBottomSheetToolbar.findViewById(R.id.act_botsheet_save);
            tbSaveButton.setOnClickListener(this::onBottomSheetSaveButtonPressed);
            tbSaveButton.setAlpha(0.25f);

            tbCloseButton = mBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_close);
            tbCloseButton.setOnMenuItemClickListener(this::onBottomSheetCloseButtonPressed);

            tbDeleteButton = mBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_delete);
            tbDeleteButton.setOnMenuItemClickListener(this::onBottomSheetDeleteButtonPressed);
            tbDeleteButton.setVisible(false);

            getLifecycle().addObserver(bottomSheetListAdapter);
            bottomSheetListAdapter.notifyDataSetChanged();

            mBottomSheetInputDate.setText(mBottomSheetDate.toString(strSimpleDateFormatPattern));
            mBottomSheetToolbarViewDate.setText(mBottomSheetDate.toString(strSimpleDateFormatPattern));
            mBottomSheetToolbarViewTitle.setText(strToolbarBottomSheetTitle);

        }

        void onBottomSheetSaveButtonPressed(View view) {
            Log.e("SAVEB", "SAVEBUTTON PRESSED");
            ButterKnife.apply(mBottomSheetInputs, REMOVE_ERRORS);

            try {
                if (damageCaseHandler.getValue() != null) {

                    Log.e("SAVEB", "not null");
                    long id = damageCaseHandler.getValue()
                            .setNameDamageCase(getIfNotEmptyElseThrow(mBottomSheetInputTitle))
                            .setAreaCode(getIfNotEmptyElseThrow(mBottomSheetInputLocation))
                            .setNamePolicyholder(getIfNotEmptyElseThrow(mBottomSheetInputPolicyholder))
                            .setNameExpert(getIfNotEmptyElseThrow(mBottomSheetInputExpert))
                            .setDate(damageCaseDate)
                            .setAreaSize(sopraMap.getArea())
                            .setCoordinates(sopraMap.getActivePoints())
                            .save();

                    fireCloseEvent();

                }
            } catch (EditFieldValueIsEmptyException e) {
                e.showError();
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }

        private String getIfNotEmptyElseThrow(EditText editText) throws EditFieldValueIsEmptyException {
            String text = editText.getText().toString();
            if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
            return text;
        }

        boolean onBottomSheetCloseButtonPressed(MenuItem menuItem) {
            Log.i("BS", "onBottomSheetCloseButtonPressed");
            showCloseAlertIfChanged();
            return true;
        }

        boolean onBottomSheetDeleteButtonPressed(MenuItem menuItem) {
            Log.i("BS", "onBottomSheetDeletedButtonPressed");
            showDeleteAlert();
            return true;
        }

        private void showCloseAlertIfChanged() {
            if ((damageCaseHandler.getValue() != null && damageCaseHandler.getValue().isChanged())) {
                showCloseAlert();
            } else {
                fireCloseEvent();
            }
        }

        private void showDeleteAlert() {
            new FixedDialog(getContext())
                    .setTitle(strBottomSheetDeleteDialogHeader)
                    .setMessage(strBottomSheetDeleteDialogMessage)
                    .setCancelable(false)
                    .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) ->
                    {
                        damageCaseHandler.deleteCurrent();
                        fireCloseEvent();
                    })
                    .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {
                    })
                    .create()
                    .show();
        }


        public void fireCloseEvent() {
            close();

            if (gpsService != null)
                gpsService.stopSingleCallback();

            EventBus.getDefault().post(new EventsBottomSheet.Close());

        }

        private void showCloseAlert() {
            new FixedDialog(getContext())
                    .setTitle(strBottomSheetCloseDialogHeader)
                    .setMessage(strBottomSheetCloseDialogMessage)
                    .setCancelable(false)
                    .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> {
                        EventBus.getDefault().post(new EventsBottomSheet.ForceClose());
                        fireCloseEvent();
                    })
                    .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {
                    })
                    .create()
                    .show();
        }

        @Override
        public void close() {
            getLifecycle().removeObserver(bottomSheetListAdapter);
            mBottomSheetBehavior.setHideable(true);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            bottomSheetListAdapter.setOnItemCountChanged(null);
            bottomSheetListAdapter = null;
            currentBottomSheet = null;
        }

        @Override
        public TYPE getType() {
            return TYPE.DAMAGE_CASE_NEW;
        }

        @Override
        public void show() {
            mBottomSheetBehavior.setHideable(false);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mBottomSheetContainer.addView(thisBottomSheetView);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

        @SuppressWarnings("ConstantConditions")
        @OnClick(R.id.bottom_sheet_input_title)
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

        @Override
        public void onItemCountChanged(int newItemCount) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            boolean enabled = newItemCount > 2;

            tbSaveButton.setEnabled(enabled);
            tbSaveButton.setAlpha(enabled ? 1 : 0.25f);
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

    }

    class BottomSheetDamageCase extends BottomSheetNewDamageCase {

        public BottomSheetDamageCase(DamageCase damageCase) {
            super();
            tbDeleteButton.setVisible(true);

            currentBottomSheet = this;

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

        @Override
        public TYPE getType() {
            return TYPE.DAMAGE_CASE;
        }
    }
}
