package de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.uni_stuttgart.informatik.sopra.sopraapp.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.MainActivityModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.login.LoginActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivity;

@Module
public abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = {MainActivityModule.class, ViewModelModule.class})
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract ProfileActivity contributeProfileActivity();

}
