package de.uni_stuttgart.informatik.sopra.sopraapp.app;


import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.FragmentScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCaseListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.DamageCaseListFragmentModule;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragmentModule;

/**
 * Here goes all the stuff provided for the MainActivity, to be injected.
 */
@Module
public abstract class MainActivityModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = {DamageCaseListFragmentModule.class})
    abstract DamageCaseListFragment contributeDamageCaseFragment();

    @ActivityScope
    @Provides
    static DamageCaseListFragment providesDamageCaseListFragment(){
        return new DamageCaseListFragment();
    }

    @FragmentScope
    @ContributesAndroidInjector(modules = {MapFragmentModule.class})
    abstract MapFragment contributeMainFragment();

    @ActivityScope
    @Provides
    static MapFragment providesMainFragment(){
        return new MapFragment();
    }

}
