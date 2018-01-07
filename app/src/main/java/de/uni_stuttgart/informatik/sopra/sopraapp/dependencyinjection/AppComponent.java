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
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.Converters;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.DatabaseManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.ContractDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.UserDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.gson.GsonModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase.DamageCaseListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.insurance.ContractListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.BottomSheetListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.contract.BottomSheetContract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.damagecase.BottomSheetDamagecase;

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

    void inject(SopraMap __);

    void inject(Converters __);

    void inject(InputRetriever __);

    void inject(EditFieldValueException __);

    void inject(DamageCase.Builder __);
    void inject(Contract.Builder __);

    void inject(DamageCase __);
    void inject(Contract __);
    void inject(User __);

    void inject(DamageCaseHandler __);
    void inject(ContractHandler __);

    void inject(DamageCaseRepository __);
    void inject(ContractRepository __);

    void inject(BottomSheetListAdapter __);

    void  inject(BottomSheetContract __);
    void  inject(BottomSheetDamagecase __);

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

    @ApplicationScope //TODO move this to a lower level
    @Provides
    public static DamageCaseHandler provideDamageCaseHandler(SopraApp app) {
        return new DamageCaseHandler(app);
    }

    @ApplicationScope //TODO move this to a lower level
    @Provides
    public static ContractHandler provideContractHandler(SopraApp app) {
        return new ContractHandler(app);
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
    static GpsService provideGpsService(SopraApp sopraApp) {
        return new GpsService(sopraApp);
    }

    @Binds
    abstract Application application(SopraApp sopraApp);


}
