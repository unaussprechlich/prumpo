package de.uni_stuttgart.informatik.sopra.sopraapp.util;


import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;

@Module
public abstract class GsonModule {

    @ApplicationScope
    @Provides
    static Gson providesGson(){
        return new Gson();
    }
}
