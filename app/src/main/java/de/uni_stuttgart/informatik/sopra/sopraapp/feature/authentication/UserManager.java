package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;


@ApplicationScope
public class UserManager {

    private LiveData<User> currentUser = null;
    private SopraApp context;

    /**
     * Yes, I abuse LiveData to be an Eventbus .... I don't have time to wait a week until
     * a useful library like http://greenrobot.org/eventbus/ is accepted.
     */
    private MutableLiveData<LiveData<User>> logoutEvent = new MutableLiveData<>();
    private MutableLiveData<User> loginEvent  = new MutableLiveData<>();

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

    public void subscribeToLogin(@NonNull LifecycleOwner owner, @NonNull Observer<User> callback){
        loginEvent.observe(owner, callback);
    }

    public void subscribeToLogout(@NonNull LifecycleOwner owner, @NonNull Observer<LiveData<User>> callback){
        logoutEvent.observe(owner, callback);
    }

    public void login(@NonNull LiveData<User> currentUser){
        this.currentUser = currentUser;
        loginEvent.postValue(currentUser.getValue());
    }

    public void logout(){
        logoutEvent.postValue(currentUser);
        this.currentUser = null;
        //TODO testing only
        //This is how you clean all activities and all the other junk ... trust me i found this on stackoverflow .. this can't be wrong!!111!111!!
        System.exit(1);
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
