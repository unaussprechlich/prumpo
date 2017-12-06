package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import dagger.Module;
import dagger.Provides;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.FragmentScope;

@Module
public class MapFragmentModule {

    @FragmentScope
    @Provides
    public DamageCaseHandler provideDamageCaseHandler(SopraApp app){
        return new DamageCaseHandler(app);
    }
}
