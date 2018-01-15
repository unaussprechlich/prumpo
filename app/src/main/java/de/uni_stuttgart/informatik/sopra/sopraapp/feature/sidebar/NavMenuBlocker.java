package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar;

import android.view.MenuItem;

public class NavMenuBlocker implements MenuItem.OnActionExpandListener {

    private NavigationDrawLocker navigationDrawLocker;

    public NavMenuBlocker(NavigationDrawLocker navigationDrawLocker) {
        this.navigationDrawLocker = navigationDrawLocker;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        lock();
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        unlock();
        return true;
    }

    private void lock() {
        navigationDrawLocker.setDrawerEnabled(false);
    }

    public void unlock() {
        navigationDrawLocker.setDrawerEnabled(true);
    }
}
