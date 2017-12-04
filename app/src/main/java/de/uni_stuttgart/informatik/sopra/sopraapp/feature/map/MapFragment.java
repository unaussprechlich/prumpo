package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.MapPoint;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.TEST_POLYGON_COORDINATES;

public class MapFragment extends DaggerFragment implements FragmentBackPressed {

    // TODO: cover case of lost ACCESS_FINE_LOCATION permissions during runtime

    @Inject
    GpsService gpsService;

    View rootView;
    MapView mMapView;

    /**
     * The adapter for the horizontal recycler view
     */
    BottomSheetListAdapter bottomSheetListAdapter;
    EditText dc_title;
    EditText dc_location;
    EditText dc_policyholder;
    EditText dc_expert;
    EditText dc_date;
    private SopraMap sopraMap;
    private boolean waitingForResponse;
    private boolean isGpsServiceBound;
    /**
     * The provided bottom sheet behaviour object
     */
    private BottomSheetBehavior mBottomSheetBehavior;
    /**
     * The recycler view for the horizontal recycler view
     */
    private RecyclerView bottomSheetRecyclerView;
    /**
     * The toolbar of the bottom sheet
     */
    private Toolbar bottomSheetToolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // guard clause for 2nd visit
        if (rootView != null) return rootView;

        rootView = inflater.inflate(R.layout.activity_main_fragment_mapview, container, false);

        mMapView = rootView.findViewById(R.id.mapV);
        mMapView.onCreate(savedInstanceState);

        // to assure immediate display
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this::onMapReady);

        // init bottom sheet
        initBottomSheet();

        return rootView;
    }

    private void onMapReady(GoogleMap googleMap) {
        sopraMap = new SopraMap(googleMap, getResources());

        sopraMap.drawPolygonOf(TEST_POLYGON_COORDINATES, PolygonType.DAMAGE_CASE);
        sopraMap.mapCameraJump(TEST_POLYGON_COORDINATES);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddMark);
        fabAdd.setOnClickListener(v -> {

            /* open bottom sheet for testing purposes, will be moved to another file? TODO <-*/
            loadDamageCaseBottomSheet(null);

            /* GPS/Map-related section */

            if (gpsService.wasLocationDisabled()) {
                promptEnableLocation();
                return;
            }

            if (waitingForResponse) return;

            LocationCallbackListener lcl = new LocationCallbackListener() {
                @Override
                public void onLocationFound(Location location) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    Toast.makeText(getContext(),
                            String.format("Latitude %s\nLongitude %s", lat, lng),
                            Toast.LENGTH_LONG)
                            .show();

                    waitingForResponse = false;
                }

                @Override
                public void onLocationNotFound() {
                    Toast.makeText(getContext(),
                            "Es konnten keine Positionsdaten im Zeitrahmen von 10 Sekunden empfangen werden.",
                            Toast.LENGTH_LONG)
                            .show();
                    waitingForResponse = false;
                }
            };

            waitingForResponse = true;
            gpsService.singleLocationCallback(lcl, 10000);
        });

        FloatingActionButton fabLocate = view.findViewById(R.id.fabLocate);
        fabLocate.setOnClickListener(v -> {
            LatLng targetPos = gpsService.lastKnownLocation();

            if (gpsService.wasLocationDisabled()) {
                promptEnableLocation();
                return;
            }

            if (targetPos == null) return;

            sopraMap.mapCameraMove(gpsService.lastKnownLocation());
        });

        // Set title of app bar
        getActivity().setTitle(R.string.map);
    }

    @Override
    public void onStart() {
        super.onStart();

        bindServices();
    }

    @Override
    public void onStop() {
        super.onStop();

        unbindServices();
    }

    private void bindServices() {
        isGpsServiceBound = true;
        gpsService.startGps();
    }

    private void unbindServices() {
        if (!isGpsServiceBound) return;

        gpsService.stopGps();
        isGpsServiceBound = false;
    }

    private void promptEnableLocation() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        getActivity().runOnUiThread(() -> Toast
                .makeText(getContext(),
                        R.string.prompt_enable_localization,
                        Toast.LENGTH_LONG)
                .show()
        );
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
     * Sets up bottom sheet
     */
    private void initBottomSheet() {

        // set bottom sheet
        NestedScrollView bottomSheet = rootView.findViewById(R.id.bottom_sheet);

        // set bottom sheet toolbar
        bottomSheetToolbar = rootView.findViewById(R.id.bottom_sheet_toolbar);

        // inflate toolbar
        bottomSheetToolbar.inflateMenu(R.menu.bottom_sheet);

        // set recycler view
        bottomSheetRecyclerView = bottomSheet.findViewById(R.id.bottom_sheet_list);
        bottomSheetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        rootView.findViewById(R.id.bottom_sheet_toolbar)

        // create bottom sheet behaviour
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // hide Bottom Sheet
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // control the state of the bottom sheet
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                MainActivity activity = (MainActivity) getActivity();

                boolean navigationDrawerEnabled = false;

                switch (newState) {

                    case BottomSheetBehavior.STATE_HIDDEN:
                        navigationDrawerEnabled = true;
                        bottomSheetRecyclerView.setAdapter(null);
                        dc_title.setText("");
                        dc_policyholder.setText("");
                        dc_expert.setText("");
                        dc_date.setText("");
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

                /* Will add some listeners later
                 - to avoid closing it by collapsing
                 - Remove menu icon if opened to avoid hinting that a nav menu exist when adding damages
                 - etc ...
                 */

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

        });


        dc_title = bottomSheet.findViewById(R.id.bs_control_title_input);
        dc_title.setOnClickListener(InputRetriever.of(dc_title)
                .withTitle(R.string.map_frag_botsheet_dialog_dc_name)
                .withHint(R.string.map_frag_botsheet_dialog_dc_name_hint)
        );

        dc_location = bottomSheet.findViewById(R.id.bs_control_location_input);
        dc_location.setOnClickListener(InputRetriever.of(dc_location)
                .withTitle(R.string.map_frag_botsheet_dialog_dc_location)
                .withHint(R.string.map_frag_botsheet_dialog_dc_location_hint)
        );

        dc_policyholder = bottomSheet.findViewById(R.id.bs_control_policyholder_input);
        dc_policyholder.setOnClickListener(InputRetriever.of(dc_policyholder)
                .withTitle(R.string.map_frag_botsheet_dialog_dc_policyholder)
                .withHint(R.string.map_frag_botsheet_dialog_dc_policyholder_hint)
        );
        dc_expert = bottomSheet.findViewById(R.id.bs_control_expert_input);
        dc_expert.setOnClickListener(InputRetriever.of(dc_expert)
                .withTitle(R.string.map_frag_botsheet_dialog_dc_expert)
                .withHint(R.string.map_frag_botsheet_dialog_dc_expert_hint)
        );

        dc_date = bottomSheet.findViewById(R.id.bs_control_date_input);
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dc_date.setText(sdf.format(myCalendar.getTime()));
            }

        };

        dc_date.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        // init toolbar close button
        View tbCloseButton = bottomSheetToolbar.findViewById(R.id.act_botsheet_close);
        tbCloseButton.setOnClickListener(v -> {
            boolean isImportantChanged = true;

            if (isImportantChanged) {
                showCloseAlert();

            } else {

                mBottomSheetBehavior.setHideable(true);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });
    }

    private void loadDamageCaseBottomSheet(DamageCase damageCase) {

        int state = mBottomSheetBehavior.getState();
        if (state == BottomSheetBehavior.STATE_HIDDEN) {

            // set state 1st time
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // lock hide mode
            mBottomSheetBehavior.setHideable(false);

            // set new adapter
            bottomSheetListAdapter = new BottomSheetListAdapter(damageCase);
            bottomSheetRecyclerView.swapAdapter(bottomSheetListAdapter, false);

            // Add first location point
            bottomSheetListAdapter.add(new MapPoint(""));

            // measure height of toolbar and recycler view
            bottomSheetToolbar.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            bottomSheetRecyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mBottomSheetBehavior.setPeekHeight(bottomSheetToolbar.getMeasuredHeight() + bottomSheetRecyclerView.getMeasuredHeight());

            // set state 2nd time
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {

            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // add next point
            bottomSheetListAdapter.add(new MapPoint(""));

            // scroll to last added item
            bottomSheetRecyclerView.smoothScrollToPosition(bottomSheetListAdapter.getItemCount() - 1);
        }
    }

    private void showCloseAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.map_frag_botsheet_alert_title)
                .setMessage(R.string.map_frag_botsheet_alert_text)
                .setCancelable(false)
                .setPositiveButton(R.string.map_frag_botsheet_alert_yes, (dialog, id) -> {
                    mBottomSheetBehavior.setHideable(true);
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                })
                .setNegativeButton(R.string.map_frag_botsheet_alert_no, (dialog, id) -> {

                })
                .create()
                .show();
    }
}
