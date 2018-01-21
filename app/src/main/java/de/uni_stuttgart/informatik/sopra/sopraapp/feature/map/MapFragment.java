package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.view.*;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.maps.MapsInitializer;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.contract.BottomSheetContract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.damagecase.BottomSheetDamagecase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unchecked")
@SuppressLint("SetTextI18n")
public class MapFragment
        extends MapBindFragment
        implements FragmentBackPressed, LocationCallbackListener, IBottomSheetOwner {

    @Inject
    GpsService gpsService;

    // TODO: cover case of lost ACCESS_FINE_LOCATION permissions during runtime

    @Inject DamageCaseRepository damageCaseRepository;
    @Inject DamageCaseHandler damageCaseHandler;
    @Inject UserManager userManager;

    @BindView(R.id.bottom_sheet_container)
    NestedScrollView nestedScrollView;

    private AtomicBoolean callbackDone = new AtomicBoolean(true);

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
        setHasOptionsMenu(true);
        initMapView(savedInstanceState);

        onResume();

        setupBottomSheetBehaviour();

        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_toolbar_top, menu);

        MenuItem addMenuItem = menu.findItem(R.id.action_add);
        addMenuItem.setOnMenuItemClickListener(this::onAddButtonClicked);
    }

    // todo remove this method
    private boolean onAddButtonClicked(MenuItem menuItem) {

        final Dialog d = new Dialog(getContext());
        d.setContentView(R.layout.activity_main_fragment_add_dialog);
        d.setTitle("Custom Dialog");
        Button addDc = d.findViewById(R.id.map_frag_dialog_add_dc);
        Button addInsurance = d.findViewById(R.id.map_frag_dialog_add_insurance);
        Button abortButton = d.findViewById(R.id.map_frag_dialog_abort);

        addDc.setOnClickListener(v -> {
            openBottomSheet(DamageCase.class);
            d.dismiss();
        });

        addInsurance.setOnClickListener(v -> {
            openBottomSheet(Contract.class);
            d.dismiss();
        });

        abortButton.setOnClickListener(v -> d.dismiss());
        d.show();
        return true;
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

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String preferencesString = preferences.getString(strPreferenceMapViewType, strPreferenceMapViewTypeDefault);
            Integer viewType = Integer.valueOf(preferencesString);
//            sopraMap = new SopraMap(googleMap, getContext(), viewType);
            sopraMap = new SopraMap(googleMap, getContext());

            getLifecycle().addObserver(sopraMap);

            sopraMap.areaLiveData().observe(this, area -> {
                currentBottomSheet.displayCurrentAreaValue(area);
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
    public void onCloseBottomSheet(EventsBottomSheet.Close event) {
        currentBottomSheet = null;
    }

    @Override
    public BackButtonProceedPolicy onBackPressed() {
        Log.i("onBackButtonPressed", "init");
        if (currentBottomSheet != null && currentBottomSheet.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            return BackButtonProceedPolicy.SKIP_ACTIVITY;
        }
        return BackButtonProceedPolicy.WITH_ACTIVITY;
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

    public void openBottomSheet(Class clazz ){
        openBottomSheet(clazz, null);
    }

    public <Model extends ModelDB> void openBottomSheet(Class clazz, Model model){

        if(currentBottomSheet != null) {
            currentBottomSheet.close();
            currentBottomSheet = null;
        }

        if(clazz == DamageCase.class){
            currentBottomSheet = new BottomSheetDamagecase(this, (Contract) model);
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

    @Subscribe
    void onBottomSheetCloseEvent(EventsBottomSheet.Close event){
        currentBottomSheet = null;
    }
}
