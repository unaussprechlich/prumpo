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
import de.uni_stuttgart.informatik.sopra.sopraapp.database.Converters;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.DatabaseManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntityDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntityRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseEntityDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntityDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntityRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract.ContractListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase.DamageCaseListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.user.UserListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.contract.BottomSheetContract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.damagecase.BottomSheetDamagecase;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.AnimationHelper;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.GsonModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.InputRetriever;

@ApplicationScope
@Component(
        modules = {
                AndroidInjectionModule.class,
                AndroidSupportInjectionModule.class,
                AppModule.class,
                ActivityBuilderModule.class,
                GsonModule.class
        }
)
public interface AppComponent extends AndroidInjector<SopraApp> {

    void inject(DamageCaseListAdapter __);
    void inject(ContractListAdapter __);
    void inject(UserListAdapter __);

    void inject(SopraMap __);

    void inject(Converters __);

    void inject(InputRetriever __);

    void inject(EditFieldValueException __);

    void inject(DamageCaseEntity.Builder __);
    void inject(ContractEntity.Builder __);

    void inject(DamageCaseEntity __);
    void inject(ContractEntity __);
    void inject(UserEntity __);

    void inject(DamageCaseHandler __);
    void inject(ContractHandler __);
    void inject(UserHandler __);
    void inject(NoUserException __);

    void inject(DamageCaseRepository __);
    void inject(ContractEntityRepository __);

    void inject(BottomSheetListAdapter __);

    void inject(BottomSheetContract __);
    void inject(BottomSheetDamagecase __);



    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<SopraApp> {

        @Override
        abstract public AppComponent build();
    }
}

/**
 * This module provides the instance of SopraApp for dependency injection.
 */
@Module
abstract class AppModule {

    @ApplicationScope
    @Provides
    public static DamageCaseHandler provideDamageCaseHandler() {
        return new DamageCaseHandler();
    }

    @ApplicationScope
    @Provides
    public static ContractHandler provideContractHandler() {
        return new ContractHandler();
    }

    @ApplicationScope
    @Provides
    public static UserHandler provideUserHandler(SopraApp app, UserRepository userRepository, UserEntityRepository userEntityRepository) {
        return new UserHandler(app, userRepository, userEntityRepository);
    }

    @ApplicationScope
    @Provides
    public static UserRepository provideUserRepository(UserDao userDao) {
        return new UserRepository(userDao);
    }

    @ApplicationScope
    @Provides
    public static AnimationHelper provideAnimationHelper() {
        return new AnimationHelper();
    }

    @Provides
    static BottomSheetListAdapter provides(){
        return new BottomSheetListAdapter();
    }

    @Provides
    @ApplicationScope
    static Context provideContext(Application application) {
        return application;
    }

    @ApplicationScope
    @Provides
    static Vibrator provideVibrator(Application application) {
        return (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
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
    static ContractDao provideContractDao(DatabaseManager db) {
        return db.contractDao();
    }

    @Provides
    @ApplicationScope
    static UserDao provideUserDao(DatabaseManager db) {
        return db.userDao();
    }

    @Provides
    @ApplicationScope
    static DamageCaseEntityDao provideDamageCaseEntityDao(DatabaseManager db) {
        return db.damageCaseEntityDao();
    }

    @Provides
    @ApplicationScope
    static ContractEntityDao provideContractEntityDao(DatabaseManager db) {
        return db.contractEntityDao();
    }

    @Provides
    @ApplicationScope
    static UserEntityDao provideUserEntityDao(DatabaseManager db) {
        return db.userEntityDao();
    }

    @Provides
    @ApplicationScope
    static GpsService provideGpsService(SopraApp sopraApp) {
        return new GpsService(sopraApp);
    }

    @Binds
    abstract Application application(SopraApp sopraApp);


}
