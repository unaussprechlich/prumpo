package de.uni_stuttgart.informatik.sopra.sopraapp.app;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.inject.Inject;

import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract.ContractListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase.DamageCaseListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.user.UserListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.about.AboutFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.settings.SettingsFragment;

abstract public class AbstractMainActivity extends BaseEventBusActivity {

    @Inject
    MapFragment mapFragment;
    @Inject
    ContractListFragment contractListFragment;
    @Inject
    DamageCaseListFragment damageCaseListFragment;
    @Inject
    UserListFragment userListFragment;
    @Inject
    SettingsFragment settingsFragment;
    @Inject
    AboutFragment aboutFragment;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private Supplier<Stream<Fragment>> fragmentsStreamSupplier =
            () -> Stream.of(damageCaseListFragment, contractListFragment, mapFragment,
                    userListFragment, settingsFragment, aboutFragment);

    // ### OnBackPressed action #######################################################################################

    /**
     * When hitting the Android back button
     */
    @Override
    public void onBackPressed() {

        // 1: If drawer open -> Close -> Consume BackPress
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        // Invoke active fragments BackPress
        FragmentBackPressed activeFragment = (FragmentBackPressed) getCurrentlyActiveFragment();
        boolean shouldPerformBackpress = activeFragment.shouldPerformBackpress();

        // 2: If fragment consumed BackPress -> Stop
        if (!shouldPerformBackpress) return;

        // 3: Switch to recent Fragment
        if (fragmentStack.size() > 1) {
            fragmentStack.pop();
            switchToFragment(fragmentStack.pop());
            return;
        }

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
    }

    public void displayDamageCaseListFragment() {
        switchToFragment(damageCaseListFragment);
    }

    public void displayContractFragment() {
        switchToFragment(contractListFragment);
    }

    public void displayUserFragment() {
        switchToFragment(userListFragment);
    }

    public void displaySettingsFragment() {
        switchToFragment(settingsFragment);
    }

    public void displayAboutFragment() {
        switchToFragment(aboutFragment);
    }

    private Stack<Fragment> fragmentStack = new Stack<>();

    /**
     * Method which actually switches the fragment.
     * This method will automatically select the navigation item.
     * @param fragment the new fragment to display
     */
    protected void switchToFragment(Fragment fragment) {
        fragmentStack.push(fragment);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.frag_enter,
                R.anim.frag_exit,
                R.anim.frag_pop_enter,
                R.anim.frag_pop_exit);


        transaction.replace(R.id.content_main_frame, fragment);

        transaction.commit();

        drawer.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(getNavigationItemFor(fragment));

    }

    /**
     * Get a reference to the active fragment
     *
     * @return The current visible active fragment
     */
    Fragment getCurrentlyActiveFragment() {
        return fragmentsStreamSupplier.get()
                .filter(Fragment::isVisible)
                .findFirst()
                .orElse(mapFragment);
    }

    private int getNavigationItemFor(Fragment fragment) {
        if (fragment == damageCaseListFragment)
            return R.id.nav_damageCases;
        else if (fragment == contractListFragment)
            return R.id.nav_contract;
        else if (fragment == mapFragment)
            return R.id.nav_map;
        else if (fragment == userListFragment)
            return R.id.nav_users;
        else if (fragment == settingsFragment)
            return R.id.nav_settings;
        else
            return R.id.nav_about;
    }

    public ContractListFragment getContractListFragment() {
        return contractListFragment;
    }

    public DamageCaseListFragment getDamageCaseListFragment() {
        return damageCaseListFragment;
    }
}
