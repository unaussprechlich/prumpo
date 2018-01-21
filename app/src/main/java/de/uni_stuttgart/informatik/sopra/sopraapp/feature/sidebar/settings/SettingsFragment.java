package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.BindString;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

public class SettingsFragment extends AbstractDaggerPreferenceFragment implements FragmentBackPressed {

    @BindString(R.string.nav_appbar_settings)
    String strAppbarTitle;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getActivity().setTitle(strAppbarTitle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
