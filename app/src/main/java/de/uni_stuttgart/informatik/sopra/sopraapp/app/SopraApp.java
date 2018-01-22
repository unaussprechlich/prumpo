package de.uni_stuttgart.informatik.sopra.sopraapp.app;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.AppComponent;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.DaggerAppComponent;


public class SopraApp extends DaggerApplication {

    @Inject UserHandler userHandler;

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
