package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.about;

import butterknife.BindString;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

public abstract class AboutBindings extends DaggerFragment {

    @BindString(R.string.nav_appbar_about)
    String strAppbarTitle;

}
