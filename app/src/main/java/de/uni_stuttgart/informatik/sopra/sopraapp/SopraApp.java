package de.uni_stuttgart.informatik.sopra.sopraapp;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.AppComponent;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.DaggerAppComponent;


public class SopraApp extends DaggerApplication {

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
