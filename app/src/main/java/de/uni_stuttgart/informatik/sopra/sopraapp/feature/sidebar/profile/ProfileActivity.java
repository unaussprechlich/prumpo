package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.os.Bundle;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.BaseEventBusActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;


public class ProfileActivity extends BaseEventBusActivity {

    @Inject
    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(R.string.profile_title_app_bar);

        findViewById(R.id.logout_button).setOnClickListener(v -> userManager.logout());
    }

    @Subscribe(sticky = true)
    public void handleLogin(EventsAuthentication.Login event){
        ((TextView)findViewById(R.id.user_name_text)).setText(event.user.getName());
        ((TextView)findViewById(R.id.user_role_text)).setText(event.user.getRole().toString());
    }

}
