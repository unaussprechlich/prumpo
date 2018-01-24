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

import com.google.android.gms.maps.MapsInitializer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelEntityDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntityRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseEntity;
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

@SuppressWarnings("unchecked")
@SuppressLint("SetTextI18n")
public class MapFragment
        extends MapBindFragment
        implements FragmentBackPressed, LocationCallbackListener, IBottomSheetOwner {

    @Inject GpsService gpsService;
    @Inject DamageCaseRepository damageCaseRepository;
    @Inject
    ContractEntityRepository contractEntityRepository;
    @Inject DamageCaseHandler damageCaseHandler;
    @Inject ContractHandler contractHandler;

    @BindView(R.id.bottom_sheet_container)
    NestedScrollView nestedScrollView;

    private SopraMap sopraMap;
    private View mRootView;
    private boolean isGpsServiceBound;

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
        damageCaseHandler.getLiveData().observe(this, damageCase -> {
            if(damageCase == null || damageCase.getID() != event.uniqueId) return;
            damageCaseHandler.getLiveData().removeObservers(this);
            openBottomSheet(DamageCaseEntity.class);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onOpenContract(EventsPolygonSelected.Contract event){
        contractHandler.getLiveData().observe(this, contract -> {
            if(contract == null || contract.getID() != event.uniqueId) return;
            contractHandler.getLiveData().removeObservers(this);
            openBottomSheet(ContractEntity.class);
        });
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
            Integer viewType = Integer.valueOf(preferencesString);

            sopraMap = new SopraMap(googleMap, getContext(), viewType);

            getLifecycle().addObserver(sopraMap);

        });

    }

    private void updateMapView() {
        if (sopraMap == null) return;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String preferencesString = preferences.getString(strPreferenceMapViewType, strPreferenceMapViewTypeDefault);

        if (preferencesString.equals("")) {
            return;
        }

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
        // start gps
        gpsService.startGps();
        isGpsServiceBound = true;

        gpsService.ongoingLocationCallback(this);
    }

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

    public <Model extends ModelEntityDB> void openBottomSheet(Class<Model> clazz){

        if(currentBottomSheet != null) {
            currentBottomSheet.close();
            currentBottomSheet = null;
        }

        if(clazz == DamageCaseEntity.class){
            currentBottomSheet = new BottomSheetDamagecase(this);
            showCurrentBottomSheet();
        } else if(clazz == ContractEntity.class){
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
    public boolean shouldPerformBackpress() {
        if(currentBottomSheet != null) currentBottomSheet.showCloseAlert();
        return currentBottomSheet == null;
    }
}
