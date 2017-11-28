package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(R.string.profile_title_app_bar);
    }

}
