package de.uni_stuttgart.informatik.sopra.sopraapp.app;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.AppComponent;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.DaggerAppComponent;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;


public class SopraApp extends DaggerApplication {

    @Inject
    UserManager userManager;

    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        SopraApp.appComponent = (AppComponent) DaggerAppComponent.builder().create(this);
        return SopraApp.appComponent;
    }

}
