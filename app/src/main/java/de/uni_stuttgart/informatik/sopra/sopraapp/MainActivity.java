package de.uni_stuttgart.informatik.sopra.sopraapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.base.BaseActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCaseListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivity;

@ApplicationScope
public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationDrawLocker{

    public static final int REQUEST_LOCATION_PERMISSION = 202;
    @Inject
    UserManager userManager;
    @Inject DamageCaseListFragment damageCaseListFragment;
    @Inject MapFragment mapFragment;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;


    /**
     * When Activity gets created
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_closed
        );

        drawer.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerSlideAnimationEnabled(true);
        drawerToggle.syncState();

        displayFragment(R.id.nav_map);
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
        FragmentBackPressed activeFragment = (FragmentBackPressed) getCurrentlyActiveFragment();
        FragmentBackPressed.BackButtonProceedPolicy proceedPolicy = activeFragment.onBackPressed();

        if (proceedPolicy == FragmentBackPressed.BackButtonProceedPolicy.SKIP_ACTIVITY)
            return;

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
                fragment = damageCaseListFragment;
                break;

            default:
                fragment = mapFragment;
                break;
        }

        getSupportFragmentManager()
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
                            REQUEST_LOCATION_PERMISSION
                    );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    return;

                checkPermissions();
            }
        }
    }

    /**
     * Method which allows fragments to lock the navigation drawer.
     *
     * @param enabled - If true -> navigation drawer enabled,
     *                if false -> navigation drawer disabled
     */
    @Override
    public void setDrawerEnabled(boolean enabled) {

        // lock or unlock drawer
        int lockMode = enabled
                ? DrawerLayout.LOCK_MODE_UNLOCKED
                : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);

        // set drawer indicator
        drawerToggle.setDrawerIndicatorEnabled(enabled);

        // sync state
        drawerToggle.syncState();
    }

    /**
     * Get a reference to the active fragment
     *
     * @return The current visible active fragment
     */
    public Fragment getCurrentlyActiveFragment() {

        if (damageCaseListFragment.isVisible())
            return damageCaseListFragment;

        return mapFragment;
    }
}
