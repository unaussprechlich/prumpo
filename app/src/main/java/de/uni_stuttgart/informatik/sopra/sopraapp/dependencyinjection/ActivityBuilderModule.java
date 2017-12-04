package de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivityModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.viewmodel.ViewModelModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.AuthenticationActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.LoginActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.SignUpActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile.ProfileActivityModule;

@Module
abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ActivityScope
    @ContributesAndroidInjector
    abstract SignUpActivity contributeSignUpActivity();

    @ActivityScope
    @ContributesAndroidInjector
    abstract AuthenticationActivity contributeAuthenticationActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = {MainActivityModule.class, ViewModelModule.class})
    abstract MainActivity contributeMainActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = {ProfileActivityModule.class})
    abstract ProfileActivity contributeProfileActivity();

}










 