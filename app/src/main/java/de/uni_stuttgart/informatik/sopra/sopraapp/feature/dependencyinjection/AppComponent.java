package de.uni_stuttgart.informatik.sopra.sopraapp.feature.dependencyinjection;


import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.DatabaseManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.UserDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCaseListFragmentRecyclerViewAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;

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
    void inject(DamageCaseListFragmentRecyclerViewAdapter damageCaseListFragmentRecyclerViewAdapter);

}

/**
 * This module does provide the Instance of SopraApp vor dependency injection.
 */
@Module
abstract class AppModule {

    @Provides
    @ApplicationScope
    static Context provideContext(Application application) {
        return application;
    }

    @Binds
    abstract Application application(SopraApp sopraApp);

    @Provides
    @ApplicationScope
    static UserManager userManager(SopraApp sopraApp){
        return new UserManager(sopraApp);
    }

    @NonNull
    @ApplicationScope
    @Provides
    static DatabaseManager provideDb(Application app) {
        return Room
                .databaseBuilder(app, DatabaseManager.class,"SopraApp.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @ApplicationScope
    @Provides
    static DamageCaseDao provideDamageCaseDao(DatabaseManager db) {
        return db.damageCaseDao();
    }


    @ApplicationScope
    @Provides
    static UserDao provideUserDao(DatabaseManager db) {
        return db.userDao();
    }
}
