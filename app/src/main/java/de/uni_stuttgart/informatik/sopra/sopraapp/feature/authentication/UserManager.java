package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;


@ApplicationScope
public class UserManager {

    private LiveData<User> currentUser = null;

    /**
     * Yes, I abuse LiveData to be an Eventbus .... I don't have time to wait a week until
     * a useful library like http://greenrobot.org/eventbus/ is accepted.
     */
    private MutableLiveData<User> logoutEvent = new MutableLiveData<>();
    private MutableLiveData<User> loginEvent  = new MutableLiveData<>();

    @Inject
    public UserManager(Application app) {
        startAuthenticationActivity(app);
    }

    private void startAuthenticationActivity(Context context){
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void subscribeToLogin(@NonNull LifecycleOwner owner, @NonNull Observer<User> callback){
        loginEvent.observe(owner, callback);
    }

    public void subscribeToLogout(@NonNull LifecycleOwner owner, @NonNull Observer<User> callback){
        logoutEvent.observe(owner, callback);
    }

    public void login(@NonNull LiveData<User> currentUser){
        this.currentUser = currentUser;
        loginEvent.postValue(currentUser.getValue());
    }

    public void logout(Context context){
        logoutEvent.postValue(currentUser.getValue());
        this.currentUser = null;
        startAuthenticationActivity(context);
    }

    public LiveData<User> getCurrentUserAsLiveData(@NonNull Context context) throws NoUserException {
        if(currentUser != null) return currentUser;
        startAuthenticationActivity(context);
        throw new NoUserException();
    }

    public User getCurrentUserAs(@NonNull Context context) throws NoUserException {
        if(currentUser != null) return currentUser.getValue();
        startAuthenticationActivity(context);
        throw new NoUserException();
    }



    public class NoUserException extends Exception{
        public NoUserException() {
            super("There is currently no User logged in!");
        }
    }
}
