package de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection;


import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.DatabaseManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.UserDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCaseListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

@ApplicationScope
@Component(
        modules = {
            AndroidInjectionModule.class,
            AndroidSupportInjectionModule.class,
            AppModule.class,
            ActivityBuilderModule.class
        }
)
public interface AppComponent extends AndroidInjector<SopraApp> {


    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<SopraApp>{}

    @ApplicationScope
    void inject(DamageCaseListAdapter damageCaseListAdapter);

    @ApplicationScope
    void inject(SopraMap sopraMap);

}

/**
 * This module provides the instance of SopraApp for dependency injection.
 */
@Module
abstract class AppModule {

    @Binds
    abstract Application application(SopraApp sopraApp);

    @Provides
    @ApplicationScope
    static Context provideContext(Application application) {
        return application;
    }

    @ApplicationScope
    @Provides
    static Vibrator provideVibrator(Application application){
        return (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Provides
    @ApplicationScope
    static UserManager userManager(SopraApp sopraApp) {
        return new UserManager(sopraApp);
    }

    @NonNull
    @Provides
    @ApplicationScope
    static DatabaseManager provideDb(Application app) {
        return Room
                .databaseBuilder(app, DatabaseManager.class, "SopraApp.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @ApplicationScope
    static DamageCaseDao provideDamageCaseDao(DatabaseManager db) {
        return db.damageCaseDao();
    }

    @Provides
    @ApplicationScope
    static UserDao provideUserDao(DatabaseManager db) {
        return db.userDao();
    }

    @Provides
    @ApplicationScope
    static GpsService provideGpsService(SopraApp sopraApp) {
        return new GpsService(sopraApp);
    }
}
