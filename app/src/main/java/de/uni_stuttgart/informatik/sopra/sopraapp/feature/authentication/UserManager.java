package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.dependencyinjection.scopes.ApplicationScope;


@ApplicationScope
public class UserManager {

    private LiveData<User> currentUser = null;

    public UserManager(Application app) {
        startAuthenticationActivity(app);
    }

    private void startAuthenticationActivity(Context context){
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void setCurrentUser(LiveData<User> currentUser) {
        this.currentUser = currentUser;
    }

    @Nullable
    public LiveData<User> getCurrentUser(Context context) {
        if(currentUser != null) return currentUser;
        startAuthenticationActivity(context);
        return null;
    }
}
