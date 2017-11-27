package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.service.location.Helper;

public class MapFragment extends Fragment {

    View rootView;
    MapView mMapView;

    private GoogleMap gMap;

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

        /* dummy-code ahead! */

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
                    Toast.makeText(getContext(),
                            String.valueOf(Math.round(Helper.areaOfPolygon(p.getPoints()))) + "m²",
                            Toast.LENGTH_SHORT)
                            .show());

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

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v ->
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
        );

        // Set title of app bar
        getActivity().setTitle(R.string.map);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // [Search icon not visible #1] https://stackoverflow.com/a/34799180/8596346
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // [Search icon not visible #2]
        setHasOptionsMenu(true);
    }
}
