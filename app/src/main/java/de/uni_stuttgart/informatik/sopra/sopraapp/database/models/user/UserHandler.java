package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.arch.lifecycle.LiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractModelHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.AuthenticationActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;

@ApplicationScope
public class UserHandler extends AbstractModelHandler<User, UserRepository>{

    private Context context;
    private UserRepository userRepository;


    @Inject
    public UserHandler(SopraApp sopraApp, UserRepository userRepository) {
        super();
        this.context = sopraApp;
        this.userRepository = userRepository;
        startAuthenticationActivity();
    }

    private void startAuthenticationActivity(){
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void set(User user) {
        super.set(user);
        CurrentUser.set(user);
    }

    public void login(@NonNull User user){
        loadFromDatabase(user.id);
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().postSticky(new EventsAuthentication.Login(user));
    }

    public void logout(){
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().postSticky(new EventsAuthentication.Logout(getValue()));
        set(null);

        // restart app with first activity as default main activity
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        //System.exit(0);
    }

    @Override
    public User getValue(){
        throw new UnsupportedOperationException("use UserHandler.getCurrentUser() instead");
    }


    public User getCurrentUser() throws NoUserException{
        if(super.getValue() == null) throw new NoUserException();
        return super.getValue();
    }

    @Override
    public LiveData<User> getLiveData() {
        return super.getLiveData();
    }



    @Override
    protected User createNewObject() throws NoUserException {
        throw new UnsupportedOperationException("CreateNewObject is not supported in UserHandler");
    }

    @Override
    public void createTemporaryNew() throws NoUserException {
        throw new UnsupportedOperationException("CreateNew is not supported in UserHandler");
    }

    @Override
    protected UserRepository getRepository() {
        return userRepository;
    }



}

