package de.uni_stuttgart.informatik.sopra.sopraapp.app;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.AuthenticationEvents;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCaseListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.OpenMapFragmentEvent;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivity;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.REQUEST_LOCATION_PERMISSION;

@ApplicationScope
public class MainActivity extends BaseEventBusActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationDrawLocker {

    @Inject
    MapFragment mapFragment;

    @Inject
    DamageCaseListFragment damageCaseListFragment;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private ActionBarDrawerToggle drawerToggle;

    private int fragmentCreatedCounter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set main layout
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // set navigation menu view
        navigationView.setNavigationItemSelectedListener(this);

        // set navigation menu header
        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = headerView.findViewById(R.id.nav_header);

        // set navigation header listener to display profile view
        header.setOnClickListener(view -> displayActivity(R.id.profile_layout));

        // set navigation menu drawer toggle
        drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_closed
        );

        drawer.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerSlideAnimationEnabled(true);
        drawerToggle.syncState();

        displayMapFragment(false);
        navigationView.setCheckedItem(R.id.nav_map);

        checkPermissions();
    }

    @Subscribe
    public void onOpenMapFragmentEvent(OpenMapFragmentEvent openMapFragmentEvent){
        displayMapFragment(true);
    }

    public void displayMapFragment(boolean withBackpress) {
        switchToFragment(mapFragment, withBackpress);
        navigationView.setCheckedItem(R.id.nav_map);
    }

    public void displayDamageCaseListFragment(boolean withBackPress) {
        switchToFragment(damageCaseListFragment, withBackPress);
        navigationView.setCheckedItem(R.id.nav_damageCases);
    }

    private void switchToFragment(Fragment fragment, boolean withBackPress) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.frag_enter,
                R.anim.frag_exit,
                R.anim.frag_pop_enter,
                R.anim.frag_pop_exit);

        transaction.replace(R.id.content_main_frame, fragment);

//        if (withBackPress)
//            transaction.addToBackStack("Fragment" + fragmentCreatedCounter);

        transaction.commit();

        drawer.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(fragment.getId());
    }

    @Subscribe(sticky = true)
    public void handleLogin(AuthenticationEvents.Login event) {
        ((TextView) findViewById(R.id.user_role_text)).setText(event.user.role.toString());
        ((TextView) findViewById(R.id.user_name_text)).setText(event.user.name);
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
        switch (item.getItemId()) {

            case R.id.nav_damageCases:
                displayDamageCaseListFragment(false);
                break;

            default:
                displayMapFragment(false);
                break;
        }
        return true;
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

    /**
     * Method which allows fragments to lock the navigation drawer.
     *
     * @param enabled - If true -> navigation drawer enabled,
     *                if false -> navigation drawer disabled
     */
    @Override
    public void setDrawerEnabled(boolean enabled) {

        if (!enabled && getCurrentlyActiveFragment().equals(mapFragment))
            getSupportActionBar().hide();
        else if (enabled && getCurrentlyActiveFragment().equals(mapFragment))
            getSupportActionBar().show();

        // lock or unlock drawer
        int lockMode = enabled
                ? DrawerLayout.VISIBLE
                : DrawerLayout.INVISIBLE;

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

    public int getMenuItemIDForFragment(Fragment fragment) {

        if (fragment.equals(damageCaseListFragment))
            return 1;
        return 0;
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
}
