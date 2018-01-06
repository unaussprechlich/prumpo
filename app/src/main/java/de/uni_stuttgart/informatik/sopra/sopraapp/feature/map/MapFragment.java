package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.maps.MapsInitializer;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.ABottomSheetBaseFunctions;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetContract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetDamagecase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;
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
    NestedScrollView nestedScrollView;

    private static BottomSheetMaster bottomSheetMaster;


    private Observer damageCaseObserver
            = damageCase -> updateDamageCase((DamageCase) damageCase);

    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    private LockableBottomSheetBehaviour mBottomSheetBehavior;
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

        bottomSheetMaster = new BottomSheetMaster();

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
                damageCaseHandler.createNew();
            } catch (UserManager.NoUserException e) {
                e.printStackTrace();
            }
            d.dismiss();
        });

        addInsurance.setOnClickListener(v -> {
            bottomSheetMaster.createNewContract(null);
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

        // todo check whether damage case is completely new or is edited

        if (damageCase.getDate() != null) {
            Log.e("BS", "DATE not null: " + damageCase.getDate());
            bottomSheetMaster.editDamageCase(damageCase, null);
        } else {
            Log.e("BS", "DATE null");

            bottomSheetMaster.createNewDamageCase(null);

            addVertexToActivePolygon();
        }
    }

    private void addVertexToActivePolygon() {
        LocationCallbackListener lcl = new OnAddButtonLocationCallback(getContext(), callbackDone);

        if (callbackDone.get()) {
            callbackDone.set(false);
            gpsService.singleLocationCallback(lcl, 10000);
        }
    }

    private void setUpBottomSheet() {
        mBottomSheetBehavior = LockableBottomSheetBehaviour.from(nestedScrollView);
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

            sopraMap.areaLiveData().observe(this, area -> {
                bottomSheetMaster.currentBottomSheet.displayCurrentAreaValue(area);
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
        bottomSheetMaster.currentBottomSheet = null;
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

    public class BottomSheetMaster {
        private ABottomSheetBaseFunctions currentBottomSheet = null;
        private ABottomSheetBaseFunctions.OnBottomSheetClose onBottomSheetClose
                = () -> currentBottomSheet = null;

        /**
         * @param damageCase         - Damage case to edit
         * @param onBottomSheetClose If null default on BottomSheetClose will be used.
         */
        void editDamageCase(DamageCase damageCase,
                            ABottomSheetBaseFunctions.OnBottomSheetClose onBottomSheetClose) {
            currentBottomSheet = createBottomSheetDamagecase(onBottomSheetClose);
            currentBottomSheet.editThisOne(damageCase);
            show();
        }

        /**
         * @param contract           - Contract to edit
         * @param onBottomSheetClose If null default on BottomSheetClose will be used.
         */
        void editContract(Contract contract,
                          ABottomSheetBaseFunctions.OnBottomSheetClose onBottomSheetClose) {
            currentBottomSheet = createBottomSheetContract(onBottomSheetClose);
            currentBottomSheet.editThisOne(contract);
            show();
        }

        /**
         * @param onBottomSheetClose If null default on BottomSheetClose will be used.
         */
        void createNewDamageCase(ABottomSheetBaseFunctions.OnBottomSheetClose
                                         onBottomSheetClose) {
            currentBottomSheet = createBottomSheetDamagecase(onBottomSheetClose);
            show();
        }

        /**
         * @param onBottomSheetClose If null default on BottomSheetClose will be used.
         */
        void createNewContract(ABottomSheetBaseFunctions.OnBottomSheetClose onBottomSheetClose) {
            currentBottomSheet = createBottomSheetContract(onBottomSheetClose);
            show();
        }


        /**
         * When in contract bottom sheet: Create new damage case and return to this
         */
        public void inContractCreateNewDamageCase() {


            currentBottomSheet.setOnBottomSheetClose(() -> {
                currentBottomSheet = null;
                createNewDamageCase(() -> {

                    // todo pass live contract back to bottom sheet
                    //  editContract(LiveData<Contract> , null);

                    // after todo completed: delete this line and use the uper one
                    createNewContract(null);
                });
            });
            currentBottomSheet.close();
        }

        public void inContractEditDamageCase(DamageCase damageCase){

            currentBottomSheet.setOnBottomSheetClose(() -> {
                currentBottomSheet = null;
                editDamageCase(damageCase, () -> {

                    // todo pass live contract back to bottom sheet
                    //  editContract(LiveData<Contract> , null);

                    // after todo completed: delete this line and use the uper one
                    createNewContract(null);
                });
            });
            currentBottomSheet.close();
        }

        private BottomSheetDamagecase createBottomSheetDamagecase(
                ABottomSheetBaseFunctions.OnBottomSheetClose onBottomSheetClose) {

            return new BottomSheetDamagecase(getContext(),
                    nestedScrollView,
                    mBottomSheetBehavior,
                    damageCaseHandler,
                    getLifecycle(),
                    gpsService,
                    sopraMap,
                    onBottomSheetClose == null ? this.onBottomSheetClose : onBottomSheetClose);
        }

        private BottomSheetContract createBottomSheetContract(
                ABottomSheetBaseFunctions.OnBottomSheetClose onBottomSheetClose) {

            return new BottomSheetContract(getContext(),
                    nestedScrollView,
                    mBottomSheetBehavior,
                    getLifecycle(),
                    gpsService,
                    sopraMap,
                    onBottomSheetClose == null ? this.onBottomSheetClose : onBottomSheetClose);
        }

        private void show() {
            new Handler().postDelayed(currentBottomSheet::show, 400);
        }
    }

    public static BottomSheetMaster getBottomSheetMaster() {
        return bottomSheetMaster;
    }
}
