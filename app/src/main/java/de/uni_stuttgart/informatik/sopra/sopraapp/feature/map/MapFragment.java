package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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

import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.service.location.GpsService;

import static de.uni_stuttgart.informatik.sopra.sopraapp.service.location.Helper.areaOfPolygon;

public class MapFragment extends DaggerFragment implements FragmentBackPressed {

    View rootView;
    MapView mMapView;

    GpsService gpsService;
    boolean gpsBound = false;

    private GoogleMap gMap;


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            GpsService.LocalBinder binder = (GpsService.LocalBinder) service;

            gpsService = binder.getService();
            gpsBound = true;

            Log.i("Inf", "GpsService CONNECTED!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("Inf", "GpsService DISCONNECTED!");
            gpsBound = false;
        }
    };



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

        // bind GPS service
        bindServices();

        mMapView.getMapAsync(googleMap -> {
            gMap = googleMap;
            initMap();
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: extract into features

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddMark);
        fabAdd.setOnClickListener(v -> {
            if (!gpsBound) return;

            LatLng lastLocation = gpsService.lastKnownLocation();

            if (gpsService.locationWasDisabled) {
                promptEnableLocation();
                return;
            }

            if (lastLocation == null) return;

            double lat = lastLocation.latitude;
            double lng = lastLocation.longitude;

            drawVertexOn(new LatLng(lat, lng));

            Snackbar.make(v, String.format("Latitude %s\nLongitude %s", lat, lng), Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        });

        FloatingActionButton fabLocate = view.findViewById(R.id.fabLocate);
        fabLocate.setOnClickListener(v -> {
            if (!gpsBound) return;

            LatLng targetPos = gpsService.lastKnownLocation();

            if (gpsService.locationWasDisabled) {
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
    public void onDestroy() {
        super.onDestroy();

        if (gpsBound) {
            getActivity().unbindService(mConnection);
            gpsBound = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (gpsService == null) return;
        gpsService.pauseGps();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (gpsService == null) return;

        gpsService.resumeGps();
    }


    private void bindServices() {
        if (gpsBound) return;

        Intent intent = new Intent(getContext(), GpsService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void initMap() {
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

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

    private final ArrayList<LatLng> TEST_POLYGON_COORDINATES = new ArrayList<>(
            Arrays.asList(
                    new LatLng(48.808631, 8.849357), new LatLng(48.808304, 8.853308),
                    new LatLng(48.807021, 8.853443), new LatLng(48.807157, 8.851568),
                    new LatLng(48.806494, 8.851383), new LatLng(48.806448, 8.851114),
                    new LatLng(48.806565, 8.850313), new LatLng(48.806940, 8.849134),
                    new LatLng(48.807047, 8.849072), new LatLng(48.808631, 8.849357)

            )
    );

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
        boolean meetsCondition = false;

        //noinspection ConstantConditions
        if (meetsCondition) {

            // Proceed with fragment back pressed action
            return BackButtonProceedPolicy.SKIP_ACTIVITY;
        }

        return BackButtonProceedPolicy.WITH_ACTIVITY;
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

}
