package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar;


public interface FragmentBackPressed {

    BackButtonProceedPolicy onBackPressed();

    enum BackButtonProceedPolicy {
        WITH_ACTIVITY,
        SKIP_ACTIVITY
    }
}
