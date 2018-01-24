package de.uni_stuttgart.informatik.sopra.sopraapp.app;


import android.Manifest;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.stream.Stream;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.CurrentUser;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.AuthenticationActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract.ContractShareHelper;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;

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

    @Inject UserHandler userHandler;

    /**
     * OnClick listener for Header and Profile image
     */
    private View.OnClickListener onHeaderIconPressed = v -> displayActivity(R.id.profile_layout);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            boolean enabled = CurrentUser.get().getRole() != UserEntity.EnumUserRoles.BAUER;

            // set main layout
            setContentView(R.layout.activity_main);

            ButterKnife.bind(this);

            setSupportActionBar(toolbar);

            // disable if current userEntity is BAUER
            MenuItem item = navigationView.getMenu().findItem(R.id.nav_users);
            item.setEnabled(enabled);
            item.setVisible(enabled);

            // set navigation menu view
            navigationView.setNavigationItemSelectedListener(this);

            // set navigation menu header
            View headerView = navigationView.getHeaderView(0);
            LinearLayout header = headerView.findViewById(R.id.nav_header);
            ImageButton imageButton = headerView.findViewById(R.id.nav_user_icon);

            // set navigation header listener to display profile view
            Stream.of(header, imageButton)
                    .forEach(view -> view.setOnClickListener(onHeaderIconPressed));

            // set navigation menu drawer toggle
            drawerToggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_closed
            );


            drawer.addDrawerListener(drawerToggle);
            drawerToggle.setDrawerSlideAnimationEnabled(true);
            drawerToggle.syncState();

            userHandler.getLiveData().observe(this, this::updateUserProfileHeaderView);

            displayMapFragment();
            checkPermissions();

        } catch (NoUserException e) {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Subscribe
    public void onOpenMapFragmentEvent(EventOpenMapFragment openMapFragmentEvent) {
        displayMapFragment();

        Class targetBottomSheet = openMapFragmentEvent.targetBottomSheet;

        if (targetBottomSheet != null)
            new Handler().postDelayed(() -> mapFragment.openBottomSheet(targetBottomSheet),
                    400);
    }

    private void updateUserProfileHeaderView(User user){
        if(user == null) return;

        View headerView = navigationView.getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.user_role_text)).setText(user.getEntity().getRole().toString());
        ((TextView) headerView.findViewById(R.id.user_name_text)).setText(user.getName());
        ((ImageView) headerView.findViewById(R.id.nav_user_icon)).setImageResource(Constants.PROFILE_IMAGE_RESOURCES[user.getEntity().getProfilePicture()]);
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
            case R.id.nav_contract:
                displayContractFragment();
                break;
            case R.id.nav_users:
                displayUserFragment();
                break;
            case R.id.nav_settings:
                displaySettingsFragment();
                break;
            case R.id.nav_about:
                displayAboutFragment();
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
    public void setDrawerEnabled(boolean enabled, boolean hide) {

        if (!enabled && hide) getSupportActionBar().hide();
        else getSupportActionBar().show();

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
