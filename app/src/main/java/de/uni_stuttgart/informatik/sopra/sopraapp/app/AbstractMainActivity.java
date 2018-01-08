package de.uni_stuttgart.informatik.sopra.sopraapp.app;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract.ContractListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase.DamageCaseListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivity;

import javax.inject.Inject;
import java.util.stream.Stream;

abstract public class AbstractMainActivity extends BaseEventBusActivity {

    @Inject
    MapFragment mapFragment;

    @Inject
    ContractListFragment insuranceListFragment;

    @Inject
    DamageCaseListFragment damageCaseListFragment;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;


    // ### OnBackPressed action #######################################################################################

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

    // ### Activity navigation ########################################################################################

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

    // ### Fragment navigation ########################################################################################

    public void displayMapFragment() {
        switchToFragment(mapFragment);
        navigationView.setCheckedItem(R.id.nav_map);
    }

    public void displayDamageCaseListFragment() {
        switchToFragment(damageCaseListFragment);
        navigationView.setCheckedItem(R.id.nav_damageCases);
    }

    public void displayInsuranceFragment() {
        switchToFragment(insuranceListFragment);
        navigationView.setCheckedItem(R.id.nav_insurances);
    }

    protected void switchToFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.frag_enter,
                R.anim.frag_exit,
                R.anim.frag_pop_enter,
                R.anim.frag_pop_exit);

        transaction.replace(R.id.content_main_frame, fragment);

        transaction.commit();

        drawer.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(fragment.getId());
    }

    /**
     * Get a reference to the active fragment
     *
     * @return The current visible active fragment
     */
    public Fragment getCurrentlyActiveFragment() {

        return Stream.of(damageCaseListFragment, insuranceListFragment, mapFragment)
                .filter(Fragment::isVisible)
                .findFirst()
                .orElse(mapFragment);

    }
}
