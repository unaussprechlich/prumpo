package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.AuthenticationActivity;



public class NoUserException extends Exception{

    @Inject Context context;

    public NoUserException() {
        super("There is currently no UserEntity logged in!");
        SopraApp.getAppComponent().inject(this);
        startAuthenticationActivity();
    }

    private void startAuthenticationActivity(){
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}