package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.os.Bundle;

import dagger.android.support.DaggerAppCompatActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;


public class ProfileActivity extends DaggerAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(R.string.profile_title_app_bar);
    }

}
