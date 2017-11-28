package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.OnBackPressedListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.service.location.GpsService;
import static de.uni_stuttgart.informatik.sopra.sopraapp.service.location.Helper.areaOfPolygon;

public class MapFragment extends Fragment implements OnBackPressedListener {

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

        /* dummy-code ahead! */

        // TODO: extract into features

        mMapView.getMapAsync(googleMap -> {
            gMap = googleMap;

            PolygonOptions rectOptions =
                    new PolygonOptions()
                            .add(new LatLng(48.806575, 8.856634),
                                    new LatLng(48.806545, 8.856913),
                                    new LatLng(48.806429, 8.856890),
                                    new LatLng(48.806459, 8.856608))
                            .geodesic(true)
                            .clickable(true)
                            .strokeColor(Color.RED)
                            .fillColor(Color.MAGENTA);

            Polygon polygon = googleMap.addPolygon(rectOptions);

            // show estimated area of polygon, when clicked
            gMap.setOnPolygonClickListener(p ->
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(),
                                    String.valueOf(Math.round(areaOfPolygon(p.getPoints())))+"m²",
                                    Toast.LENGTH_SHORT)
                                    .show()));


            // zooming to the location of the polygon
            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(polygon.getPoints().get(0))
                            .zoom(20)
                            .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: extract into features

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            if (!gpsBound) return;

            Location lastLocation = gpsService.getLastLocation();

            if (lastLocation == null) return;

            double lat = lastLocation.getLatitude();
            double lng = lastLocation.getLongitude();

            gMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(String.format("Latitude %s Longitude %s", lat, lng)));

            Snackbar.make(v,
                    String.format("Latitude %s\nLongitude %s", lat, lng),
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
                }
        );

        // Set title of app bar
        getActivity().setTitle(R.string.map);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mConnection != null) {
            Context applicationContext = getActivity().getApplicationContext();
            boolean isBound = applicationContext.bindService(new Intent(applicationContext, MapFragment.class), mConnection, Context.BIND_AUTO_CREATE);

            if (isBound)
                getActivity().unbindService(mConnection);
        }
    }

    private void bindServices() {
        Intent intent = new Intent(getContext(), GpsService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        Log.i("TAG", "Back");
    }

    /**
     * Indicates whether the fragment wants to controll the back button
     *
     * @return true, if fragmants wants to controll the back button
     */
    @Override
    public boolean requestBackButtonControl() {
        return false;
    }


}
