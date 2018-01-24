package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class MapFragmentModule {

    @Provides
    public static SopraMap provideSopraMap(MapFragment map) {
        return map.getSopraMap();
    }
}
