package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;


@ApplicationScope
public class UserManager {

    private LiveData<User> currentUser = null;
    private SopraApp context;

    @Inject
    public UserManager(SopraApp app) {
        this.context = app;
        startAuthenticationActivity();
    }

    private void startAuthenticationActivity(){
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void login(@NonNull LiveData<User> currentUser){
        this.currentUser = currentUser;
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().postSticky(new AuthenticationEvents.Login(currentUser.getValue()));
    }

    public void logout(){
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().postSticky(new AuthenticationEvents.Logout(currentUser.getValue()));
        this.currentUser = null;

        //TODO testing only ########################################################################
        //DANGER!
        //SHIT!
        //DON'T DO THIZZZZ
        //This is how you clean all activities and all the other junk ...
        //trust me i found this on stackoverflow .. this can't be wrong!!111!111!!
        System.exit(0);
        //TODO SHIT ################################################################################

        startAuthenticationActivity();
    }

    public LiveData<User> getCurrentUserAsLiveData() throws NoUserException {
        if(currentUser != null) return currentUser;
        startAuthenticationActivity();
        throw new NoUserException();
    }

    public User getCurrentUser() throws NoUserException {
        if(currentUser != null) return currentUser.getValue();
        startAuthenticationActivity();
        throw new NoUserException();
    }

    public class NoUserException extends Exception{
        public NoUserException() {
            super("There is currently no User logged in!");
        }
    }
}
