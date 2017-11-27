package de.uni_stuttgart.informatik.sopra.sopraapp;


import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCasesFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {


    public static final int REQUEST_LOCATION = 202;

    protected OnBackPressedListener dcOnBackPressedListener;

    private Fragment damageCasesFragment;
    private Fragment mapFragment;

    private DrawerLayout drawer;

    /**
     * When Activity gets created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = headerView.findViewById(R.id.nav_header);
        header.setOnClickListener(view -> displaySelectedActivity(R.id.profile_layout));

        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_closed
                );

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        DamageCasesFragment dcFragment = new DamageCasesFragment();
        dcFragment.setNavigationDrawer(drawer);

        damageCasesFragment = dcFragment;
        mapFragment = new MapFragment();

        displaySelectedScreen(R.id.nav_map);

        checkPermissions();
    }

    /**
     * When hitting the Android back button
     */
    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (dcOnBackPressedListener != null && dcOnBackPressedListener.needsClose()) {
            dcOnBackPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * When hitting the toolbar items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* Handle action bar item clicks here. The action bar will
         automatically handle clicks on the Home/Up button, so long
         as you specify a parent activity in AndroidManifest.xml. */

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * When hitting one of the sandwich menu items
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void displaySelectedScreen(int itemId) {
        Fragment fragment;

        switch (itemId) {

            case R.id.nav_damageCases:
                fragment = damageCasesFragment;
                break;

            default:
                fragment = mapFragment;
                break;
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_main_frame, fragment);
        fragmentTransaction.commit();

        drawer.closeDrawer(GravityCompat.START);

    }

    public void displaySelectedActivity(int itemId) {

        switch (itemId) {
            case R.id.profile_layout:
                Intent myIntent = new Intent(this, ProfileActivity.class);
                startActivity(myIntent);
                break;
        }

        // Close drawer when selecting any icon
        drawer.postDelayed(() -> drawer.closeDrawer(GravityCompat.START), 500);
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION
                    );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                checkPermissions();
            }
        }
    }

    public void setDcOnBackPressedListener(OnBackPressedListener dcOnBackPressedListener) {
        this.dcOnBackPressedListener = dcOnBackPressedListener;
    }

    @Override
    protected void onDestroy() {
        dcOnBackPressedListener = null;
        super.onDestroy();
    }

    public interface OnBackPressedListener {

        /**
         * Controll back press in fragment
         */
        void onBackPressed();

        /**
         * Tells if back button should be controlled by fragment
         *
         * @return
         */
        boolean needsClose();
    }
}
