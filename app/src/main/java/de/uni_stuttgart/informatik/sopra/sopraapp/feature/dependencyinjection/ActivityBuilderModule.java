package de.uni_stuttgart.informatik.sopra.sopraapp.feature.dependencyinjection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.AuthenticationActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.MainActivityModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.LoginActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.dependencyinjection.viewmodel.ViewModelModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivity;

@Module
abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ActivityScope
    @ContributesAndroidInjector
    abstract AuthenticationActivity contributeAuthenticationActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = {MainActivityModule.class, ViewModelModule.class})
    abstract MainActivity contributeMainActivity();

    @ActivityScope
    @ContributesAndroidInjector
    abstract ProfileActivity contributeProfileActivity();

}





 