package de.uni_stuttgart.informatik.sopra.sopraapp.app;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.REQUEST_LOCATION_PERMISSION;

@ApplicationScope
public class MainActivity
        extends AbstractMainActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationDrawLocker {

    private ActionBarDrawerToggle drawerToggle;

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

        displayMapFragment();
        navigationView.setCheckedItem(R.id.nav_map);

        checkPermissions();
    }

    @Subscribe
    public void onOpenMapFragmentEvent(EventOpenMapFragment openMapFragmentEvent) {
        displayMapFragment();
    }


    @Subscribe(sticky = true)
    public void handleLogin(EventsAuthentication.Login event) {
        if (findViewById(R.id.user_role_text) == null) return;
        // TODO: fix null binding

        ((TextView) findViewById(R.id.user_role_text)).setText(event.user.getRole().toString());
        ((TextView) findViewById(R.id.user_name_text)).setText(event.user.getName());
    }

    /**
     * When hitting one of the sandwich menu items
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_damageCases:
                displayDamageCaseListFragment();
                break;
            case R.id.nav_insurances:
                displayInsuranceFragment();
                break;
            default:
                displayMapFragment();
                break;
        }
        return true;
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
