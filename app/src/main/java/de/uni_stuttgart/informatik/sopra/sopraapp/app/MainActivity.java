package de.uni_stuttgart.informatik.sopra.sopraapp.app;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract.ContractShareHelper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;
import org.greenrobot.eventbus.Subscribe;

import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.REQUEST_LOCATION_PERMISSION;
import static de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION;

@ApplicationScope
public class MainActivity
        extends AbstractMainActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationDrawLocker {

    private ContractShareHelper contractShareHelper = null;
    private ActionBarDrawerToggle drawerToggle;

    @Nullable @BindView(R.id.user_role_text) TextView userRoleTextView;
    @Nullable @BindView(R.id.user_name_text) TextView userNameTextView;
    @Nullable @BindView(R.id.nav_user_icon)  ImageView navUserIconImageView;

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

        Class targetBottomSheet = openMapFragmentEvent.targetBottomSheet;

        if (targetBottomSheet != null)
            new Handler().postDelayed(() -> mapFragment.openBottomSheet(targetBottomSheet),
                    400);
    }


    @Subscribe(sticky = true)
    public void handleLogin(EventsAuthentication.Login event) {

        userRoleTextView.setText(event.user.getRole().toString());
        userNameTextView.setText(event.user.getName());
        navUserIconImageView.setImageResource(Constants.PROFILE_IMAGE_RESOURCES[event.user.getProfilePicture()]);
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
                displayContractFragment();
                break;
            case R.id.nav_users:
                displayUserFragment();
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
                break;
            }
            case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (contractShareHelper != null)
                        contractShareHelper.saveAsJsonFile();

                if (contractShareHelper != null)
                    contractShareHelper.requestWritePermission();
                break;
            }
        }
    }

    public void setContractShareHelper(@Nullable ContractShareHelper contractShareHelper) {
        this.contractShareHelper = contractShareHelper;
    }
}
