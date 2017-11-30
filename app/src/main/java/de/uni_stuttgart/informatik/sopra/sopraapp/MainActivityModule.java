package de.uni_stuttgart.informatik.sopra.sopraapp;


import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCaseListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCaseListFragmentModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragment;

/**
 * Here goes all the stuff provided for the MainActivity, to be injected.
 */
@Module
public abstract class MainActivityModule {


    @ContributesAndroidInjector(modules = {DamageCaseListFragmentModule.class})
    abstract DamageCaseListFragment contributeDamageCaseFragment();

    @Provides
    static DamageCaseListFragment providesDamageCaseListFragment(){
        return new DamageCaseListFragment();
    }

    @ContributesAndroidInjector
    abstract MapFragment contributeMainFragment();

    @Provides
    static MapFragment providesMainFragment(){
        return new MapFragment();
    }

}
