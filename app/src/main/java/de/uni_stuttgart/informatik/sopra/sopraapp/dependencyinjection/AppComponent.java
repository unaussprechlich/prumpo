package de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection;


import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.DatabaseManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCaseListFragmentRecyclerViewAdapter;

@Singleton
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

    void inject(DamageCaseListFragmentRecyclerViewAdapter damageCaseListFragmentRecyclerViewAdapter);

}

/**
 * This module does provide the Instance of SopraApp vor dependency injection.
 */
@Module
abstract class AppModule {


    @Provides
    @Singleton
    static Context provideContext(Application application) {
        return application;
    }

    @Binds
    abstract Application application(SopraApp sopraApp);

    @NonNull
    @Singleton
    @Provides
    static DatabaseManager provideDb(Application app) {
        return Room.databaseBuilder(app, DatabaseManager.class,"SopraApp.db").build();
    }

    @Singleton
    @Provides
    static DamageCaseDao provideDamageCaseDao(DatabaseManager db) {
        return db.damageCaseDao();
    }


    @Singleton
    @Provides
    static UserDao provideUserDao(DatabaseManager db) {
        return db.userDao();
    }
}
