package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.TEST_POLYGON_COORDINATES;
import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.Helper.areaOfPolygon;

public class MapFragment extends DaggerFragment implements FragmentBackPressed {

    // TODO: cover case of lost ACCESS_FINE_LOCATION permissions during runtime

    @Inject
    GpsService gpsService;

    View rootView;
    MapView mMapView;

    private GoogleMap gMap;
    private boolean waitingForResponse;
    private boolean isGpsServiceBound;

    /**
     * The provided bottom sheet behaviour object
     */
    private BottomSheetBehavior mBottomSheetBehavior;

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

        mMapView.getMapAsync(googleMap -> {
            gMap = googleMap;
            initMap();
        });

        // init bottom sheet
        initBottomSheet();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddMark);
        fabAdd.setOnClickListener(v -> {

            /* open bottom sheet for testing purposes, will be moved to another file? TODO <-*/
            int state = mBottomSheetBehavior.getState();
            if (state == BottomSheetBehavior.STATE_HIDDEN) {

                mBottomSheetBehavior.setHideable(false);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

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

                    Snackbar.make(v,
                            String.format("Latitude %s\nLongitude %s", lat, lng),
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();

                    drawVertexOn(new LatLng(lat, lng));
                    waitingForResponse = false;
                }

                @Override
                public void onLocationNotFound() {
                    Snackbar.make(v,
                            "Es konnten keine Positionsdaten im Zeitrahmen von 10 Sekunden empfangen werden.",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
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

            mapCameraMove(gpsService.lastKnownLocation());
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

    //  <-- TODO: extract into features -->

    private void unbindServices() {
        if (!isGpsServiceBound) return;

        gpsService.stopGps();
        isGpsServiceBound = false;
    }

    private void initMap() {
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.setIndoorEnabled(false);

        UiSettings uiSettings = gMap.getUiSettings();
        uiSettings.setTiltGesturesEnabled(false);

        drawPolygonOf(TEST_POLYGON_COORDINATES);
        mapCameraJump(TEST_POLYGON_COORDINATES.get(0));

        // show estimated area of polygon, when clicked
        gMap.setOnPolygonClickListener(p ->
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(),
                                String.valueOf(Math.round(areaOfPolygon(p.getPoints()))) + "m²",
                                Toast.LENGTH_SHORT)
                                .show()
                )
        );
    }

    private void drawPolygonOf(ArrayList<LatLng> coordinates) {
        PolygonOptions rectOptions =
                new PolygonOptions()
                        .addAll(coordinates)
                        .geodesic(true)
                        .clickable(true)
                        .strokeJointType(JointType.ROUND)
                        .strokeColor(getResources().getColor(R.color.red, null))
                        .fillColor(getResources().getColor(R.color.damage, null));

        for (LatLng point : coordinates) {
            drawVertexOn(point);
        }

        gMap.addPolygon(rectOptions);
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

    private CameraPosition cameraPosOf(LatLng target, int zoom) {
        return new CameraPosition.Builder()
                .target(target).zoom(zoom).build();
    }

    private void drawVertexOn(LatLng point) {
        gMap.addCircle(
                new CircleOptions()
                        .fillColor(getResources().getColor(R.color.contrastComplement, null))
                        .center(point)
                        .strokeWidth(4)
                        .radius(3)
                        .zIndex(1)
        );
    }

    private void mapCameraJump(LatLng target) {
        // jumping to the location of the polygon
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 18)));
    }

    private void mapCameraMove(LatLng target) {
        // panning to the location of the polygon
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosOf(target, 18)));
    }

    @Override
    public BackButtonProceedPolicy onBackPressed() {
        boolean meetsCondition = false;

        //noinspection ConstantConditions
        if (meetsCondition) {

            // Proceed with fragment back pressed action
            return BackButtonProceedPolicy.SKIP_ACTIVITY;
        }

        return BackButtonProceedPolicy.WITH_ACTIVITY;
    }


    /**
     * Sets up bottom sheet
     */
    private void initBottomSheet() {

        // find the bottom sheet in root children
        View bottomSheet = rootView.findViewById(R.id.bottom_sheet);

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

            }


        });

        // set bottom sheet toolbar
        Toolbar botsheetToolbar = rootView.findViewById(R.id.bottom_sheet_toolbar);
        botsheetToolbar.inflateMenu(R.menu.bottom_sheet);

        // init toolbar close button
        View tbCloseButton = botsheetToolbar.findViewById(R.id.act_botsheet_close);
        tbCloseButton.setOnClickListener(v -> {


            boolean isImportantChanged = true;

            if (isImportantChanged) {

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

            } else {

                mBottomSheetBehavior.setHideable(true);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }

        });

    }
}
