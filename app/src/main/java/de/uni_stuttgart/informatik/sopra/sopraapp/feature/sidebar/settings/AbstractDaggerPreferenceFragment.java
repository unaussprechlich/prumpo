package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.settings;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

import javax.inject.Inject;

public abstract class AbstractDaggerPreferenceFragment
        extends PreferenceFragmentCompat
        implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> childFragmentInjector;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
       return childFragmentInjector;
    }
}
