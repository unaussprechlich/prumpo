package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.settings;

import butterknife.BindString;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

public abstract class SettingsBindings extends AbstractDaggerPreferenceFragment {

    @BindString(R.string.nav_appbar_settings)
    String strAppbarTitle;

}
