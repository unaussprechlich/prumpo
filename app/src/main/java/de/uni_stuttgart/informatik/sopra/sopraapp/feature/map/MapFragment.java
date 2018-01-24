package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.maps.MapsInitializer;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.contract.BottomSheetContract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.damagecase.BottomSheetDamagecase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsPolygonSelected;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

@SuppressWarnings("unchecked")
@SuppressLint("SetTextI18n")
public class MapFragment
        extends MapBindFragment
        implements FragmentBackPressed, LocationCallbackListener, IBottomSheetOwner {

    @Inject GpsService gpsService;
    @Inject DamageCaseRepository damageCaseRepository;
    @Inject ContractRepository contractRepository;
    @Inject DamageCaseHandler damageCaseHandler;
    @Inject ContractHandler contractHandler;

    @BindView(R.id.bottom_sheet_container)
    NestedScrollView nestedScrollView;

    private SopraMap sopraMap;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mRootView != null)
            return mRootView;

        // the following is only called once

        mRootView = inflater.inflate(R.layout.activity_main_fragment_mapview,
                container,
                false);
        ButterKnife.bind(this, mRootView);
        initMapView(savedInstanceState);

        onResume();

        setupBottomSheetBehaviour();

        return mRootView;
    }

    //EVENT BUS ####################################################################################

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onOpenDamageCase(EventsPolygonSelected.DamageCase event) {
        damageCaseHandler.loadFromDatabase(event.uniqueId);
        new Handler().postDelayed(() -> openBottomSheet(DamageCase.class), 400);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onOpenContract(EventsPolygonSelected.Contract event){
        contractHandler.loadFromDatabase(event.uniqueId);
        new Handler().postDelayed(() -> openBottomSheet(Contract.class), 400);
    }

    @Subscribe
    public void onBottomSheetCloseEvent(EventsBottomSheet.Close event){
        currentBottomSheet = null;
    }

    //##############################################################################################

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

            /* determine map-type variant */
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String preferencesString = preferences.getString(strPreferenceMapViewType, strPreferenceMapViewTypeDefault);

            if (preferencesString == null) return;
            if (preferencesString.equals("")) return;

            Integer viewType = Integer.valueOf(preferencesString);

            sopraMap = new SopraMap(googleMap, getContext(), viewType);

            getLifecycle().addObserver(sopraMap);
        });

    }

    private void updateMapView() {
        if (sopraMap == null) return;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String preferencesString = preferences.getString(strPreferenceMapViewType, strPreferenceMapViewTypeDefault);

        if (preferencesString == null) return;
        if (preferencesString.equals("")) return;

        Integer viewType = Integer.valueOf(preferencesString);

        if (sopraMap == null) return;

        sopraMap.updateMapType(viewType);
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

    //Lifecycle ####################################################################################

    @Override
    public void onStart() {

        super.onStart();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        /* start location/gps-services */
        AtomicBoolean hasPermission = new AtomicBoolean(false);
        // retries starting/binding GpsService object until it receives permission
        gpsService.startGps(new GpsService.RetryRunUntil(() -> hasPermission.set(gpsService.startGps()), hasPermission, 1000) {
        });

        // bind to ongoing callback for estimating current user location
        gpsService.ongoingLocationCallback(this);
    }

    @Override
    public void onStop() {
        Log.i("onStop", "init");

        super.onStop();

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        gpsService.stopGps();
        gpsService.stopAllCallbacks();

        currentBottomSheet = null;
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

        // update SopraMap object after potential changes to the settings
        updateMapView();
    }

    //##############################################################################################

    private LockableBottomSheetBehaviour lockableBottomSheetBehaviour;
    private void setupBottomSheetBehaviour(){
        lockableBottomSheetBehaviour = LockableBottomSheetBehaviour.from(getNestedScrollView());
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
        lockableBottomSheetBehaviour.allowUserSwipe(false);
    }

    @Override
    public LockableBottomSheetBehaviour getLockableBottomSheetBehaviour() {
        return lockableBottomSheetBehaviour;
    }

    @Override
    public NestedScrollView getNestedScrollView() {
        return nestedScrollView;
    }

    public SopraMap getSopraMap() {
        return sopraMap;
    }

    private AbstractBottomSheetBase currentBottomSheet = null;

    public <Model extends ModelDB> void openBottomSheet(Class<Model> clazz){

        if(currentBottomSheet != null) {
            currentBottomSheet.close();
            currentBottomSheet = null;
        }

        if(clazz == DamageCase.class){
            currentBottomSheet = new BottomSheetDamagecase(this);
            showCurrentBottomSheet();
        } else if(clazz == Contract.class){
            currentBottomSheet = new BottomSheetContract(this);
            showCurrentBottomSheet();
        } else {
            throw new IllegalArgumentException("[MapFragment.openBottomSheet] There is no assignable BottomSheet for Class<"
                    + clazz.toString() + ">!");
        }
    }

    private void showCurrentBottomSheet() {
        new Handler().postDelayed(currentBottomSheet::show, 400);
    }

    @Override
    public BackButtonProceedPolicy onBackPressed() {

        /* Consume BackPress if bottom sheet is shown */
        return currentBottomSheet != null
                ? BackButtonProceedPolicy.SKIP_ACTIVITY
                : BackButtonProceedPolicy.WITH_ACTIVITY;
    }
}
