package de.uni_stuttgart.informatik.sopra.sopraapp;


import android.Manifest;
import android.app.Fragment;
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
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.OnBackPressedListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationDrawLocker {


    public static final int REQUEST_LOCATION = 202;

    private Fragment damageCasesFragment;
    private Fragment mapFragment;

    private DrawerLayout drawer;

    /**
     * When Activity gets created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set main layout
        setContentView(R.layout.activity_main);

        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set navigation menu view
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set navigation menu header
        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = headerView.findViewById(R.id.nav_header);

        // set navigation header listener to display profile view
        header.setOnClickListener(view -> displayActivity(R.id.profile_layout));

        // set navigation menu drawer
        drawer = findViewById(R.id.drawer_layout);

        // set navigation menu drawer toggle
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_closed
                );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // set fragments
        damageCasesFragment = new DamageCasesFragment();
        mapFragment = new MapFragment();

        // set initial fragment
        displayFragment(R.id.nav_map);

        /* set initial fragment as active,
        following items will be handled by fragment manager */
        navigationView.setCheckedItem(R.id.nav_map);

        checkPermissions();
    }

    /**
     * When hitting the Android back button
     */
    @Override
    public void onBackPressed() {

        // if drawer is open -> close it
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        // if active fragment wants to override back button -> perform fragment back button action
        OnBackPressedListener activeFragment = (OnBackPressedListener) getCurrentlyActiveFragment();
        if (activeFragment != null && activeFragment.requestBackButtonControll()) {
            activeFragment.onBackPressed();
            return;
        }

        // Else perform default action
        super.onBackPressed();

    }

    /**
     * When hitting one of the sandwich menu items
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayFragment(item.getItemId());
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Changes the current screen to a fragment in this activity.
     *
     * @param itemId The id of the fragment to display
     */
    public void displayFragment(int itemId) {
        Fragment fragment;

        switch (itemId) {

            case R.id.nav_damageCases:
                fragment = damageCasesFragment;
                break;

            default:
                fragment = mapFragment;
                break;
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main_frame, fragment)
                .commit();

        drawer.closeDrawer(GravityCompat.START);

    }

    /**
     * Calls another activity specified by the activity id.
     * Back navigation is automatically handled by the system
     * as long as hierarchy is specified in the manifest.
     *
     * @param itemId The id of the activity's main view.
     */
    public void displayActivity(int itemId) {

        switch (itemId) {
            case R.id.profile_layout:
                Intent myIntent = new Intent(this, ProfileActivity.class);
                startActivity(myIntent);
                break;
        }

        /* close drawer when selecting any icon.*/
        drawer.postDelayed(() -> drawer.closeDrawer(GravityCompat.START), 500);
    }

    private void checkPermissions() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;

        if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat
                    .requestPermissions(
                            this,
                            new String[]{permission},
                            REQUEST_LOCATION
                    );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    return;

                checkPermissions();
            }
        }
    }

    @Override
    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);
    }

    /**
     * Get a reference to the active fragment
     *
     * @return The current visible active fragment
     */
    public Fragment getCurrentlyActiveFragment() {

        if (damageCasesFragment.isVisible())
            return damageCasesFragment;

        return mapFragment;
    }
}
