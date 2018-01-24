package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.arch.lifecycle.LiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractModelHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.AuthenticationActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;

public class UserHandler extends AbstractModelHandler<User, UserEntity, UserRepository, UserEntityRepository>{

    private Context context;
    private UserRepository userRepository;
    private UserEntityRepository userEntityRepository;

    public UserHandler(SopraApp sopraApp, UserRepository userRepository, UserEntityRepository userEntityRepository) {
        super();
        this.context = sopraApp;
        this.userRepository = userRepository;
        this.userEntityRepository = userEntityRepository;
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
        if(user == null) CurrentUser.set(null);
        else CurrentUser.set(user.getEntity());
    }

    public void login(@NonNull UserEntity userEntity){
        loadFromDatabase(userEntity.id);
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().postSticky(new EventsAuthentication.Login(userEntity));
    }

    public void logout(){
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

    @Override
    protected UserEntityRepository getEntityRepository() {
        return userEntityRepository;
    }


}

