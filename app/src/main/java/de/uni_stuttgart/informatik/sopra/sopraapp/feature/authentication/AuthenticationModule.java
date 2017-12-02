package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;


import android.app.Application;
import android.arch.lifecycle.LiveData;

import dagger.Module;
import dagger.Provides;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.dependencyinjection.scopes.UserScope;

@Module
public abstract class AuthenticationModule {

    @UserScope
    @Provides
    LiveData<User> provideLiveUser(UserManager userManager, Application application){
        return userManager.getCurrentUser(application);
    }
}
