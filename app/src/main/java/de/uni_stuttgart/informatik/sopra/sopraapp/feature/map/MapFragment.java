package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetDamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetNewDamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetNewInsurance;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsVertex;
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

    private BottomSheetNewDamageCase.OnBottomSheetClose onBottomSheetClose
            = () -> currentBottomSheet = null;

    private Observer damageCaseObserver
            = damageCase -> updateDamageCase((DamageCase) damageCase);

    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    private LockableBottomSheetBehaviour mBottomSheetBehavior;
    private BottomSheet currentBottomSheet = null;
    private SopraMap sopraMap;
    private View mRootView;
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
        setHasOptionsMenu(true);
        setUpBottomSheet();
        initMapView(savedInstanceState);

        onResume();

        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_toolbar_top, menu);

        MenuItem addMenuItem = menu.findItem(R.id.action_add);
        addMenuItem.setOnMenuItemClickListener(this::onAddButtonClicked);
    }

    private boolean onAddButtonClicked(MenuItem menuItem) {

        final Dialog d = new Dialog(getContext());
        d.setContentView(R.layout.activity_main_fragment_add_dialog);
        d.setTitle("Custom Dialog");
        Button addDc = d.findViewById(R.id.map_frag_dialog_add_dc);
        Button addInsurance = d.findViewById(R.id.map_frag_dialog_add_insurance);
        Button abortButton = d.findViewById(R.id.map_frag_dialog_abort);

        addDc.setOnClickListener(v -> {
            try {
                damageCaseHandler.createNewDamageCase();
            } catch (UserManager.NoUserException e) {
                e.printStackTrace();
            }
            d.dismiss();
        });

        addInsurance.setOnClickListener(v -> {

            // todo check for opened bottom sheet

            currentBottomSheet = new BottomSheetNewInsurance(
                    getContext(),
                    mBottomSheetContainer,
                    mBottomSheetBehavior,
                    getLifecycle(),
                    gpsService,
                    sopraMap,
                    onBottomSheetClose);
            new Handler().postDelayed(currentBottomSheet::show, 400);
            d.dismiss();

        });

        abortButton.setOnClickListener(v -> d.dismiss());
        d.show();
        return true;
    }


    private void updateDamageCase(DamageCase damageCase) {
        Log.e("LOG", "dc");

        // todo check for opened insurance bottom sheet

        if (damageCase == null) {
            return;
        }

        if (damageCase.getNamePolicyholder().isEmpty()) {
            currentBottomSheet = new BottomSheetNewDamageCase(getContext(), mBottomSheetContainer,
                    mBottomSheetBehavior,
                    damageCaseHandler,
                    getLifecycle(),
                    gpsService,
                    sopraMap,
                    onBottomSheetClose);

            addVertexToActivePolygon();

        } else {
            currentBottomSheet = new BottomSheetDamageCase(getContext(), mBottomSheetContainer,
                    mBottomSheetBehavior,
                    damageCaseHandler,
                    getLifecycle(),
                    gpsService,
                    sopraMap,
                    damageCase,
                    onBottomSheetClose);
            BottomSheetDamageCase bsdc = (BottomSheetDamageCase) currentBottomSheet;

            for (LatLng latLng : damageCase.getCoordinates()) {
                bsdc.getBottomSheetListAdapter().add(true);
            }
        }

        new Handler().postDelayed(currentBottomSheet::show, 400);

    }

    private void addVertexToActivePolygon() {
        LocationCallbackListener lcl = new OnAddButtonLocationCallback(getContext(), callbackDone);

        if (callbackDone.get()) {
            callbackDone.set(false);
            gpsService.singleLocationCallback(lcl, 10000);
        }
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
                    bsdc.getmBottomSheetToolbarViewArea().setText("" + (double) Math.round(area * 100d) / 100d);
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
        if (currentBottomSheet == null || (currentBottomSheet.getType() != BottomSheet.TYPE.DAMAGE_CASE || currentBottomSheet.getType() != BottomSheet.TYPE.DAMAGE_CASE_NEW))
            return;

        BottomSheetNewDamageCase bsdc = (BottomSheetNewDamageCase) currentBottomSheet;
        String text = String.valueOf(sopraMap.areaLiveData().getValue());
        bsdc.getmBottomSheetToolbarViewArea().setText("null".equals(text) ? "0.0" : text);

        int target = Math.max(bsdc.getBottomSheetListAdapter().getItemCount() - 1, 0);
        bsdc.getmBottomSheetBubbleList().smoothScrollToPosition(target);
    }

    @Subscribe
    public void onVertexSelected(EventsVertex.Selected event) {
        Log.e("SUBS", "vertexSelected" + event.vertexNumber);
        if (currentBottomSheet == null
                || (currentBottomSheet.getType() != BottomSheet.TYPE.DAMAGE_CASE || currentBottomSheet.getType() != BottomSheet.TYPE.DAMAGE_CASE_NEW))
            return;


        BottomSheetNewDamageCase bsdc = (BottomSheetNewDamageCase) currentBottomSheet;
        bsdc.getmBottomSheetBubbleList().smoothScrollToPosition(event.vertexNumber);
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

    //    @OnClick(R.id.fab_plus)
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

}
