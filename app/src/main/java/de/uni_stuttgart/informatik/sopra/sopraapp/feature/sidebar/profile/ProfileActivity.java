package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.BaseActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;


public class ProfileActivity extends BaseActivity {

    @Inject
    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(R.string.profile_title_app_bar);

        userManager.subscribeToLogin(this,this::updateText);
        findViewById(R.id.logout_button).setOnClickListener(v -> userManager.logout(this));
    }

    private void updateText(User user){
        ((TextView)findViewById(R.id.user_name_text)).setText(user.name);
        ((TextView)findViewById(R.id.user_role_text)).setText(user.role.toString());
    }

}
