package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar;


public interface FragmentBackPressed {

    /**
     * Method which got called when pressing the back button.
     * If returned WITH_ACTIVITY the activity back press will be triggered.
     * If returned SKIP_ACTIVITY the activity back press will be ignored.
     * That actually depends on the implementation in the activity.
     *
     * @return how to continue with back press handle.
     */
    default BackButtonProceedPolicy onBackPressed() {
        return BackButtonProceedPolicy.WITH_ACTIVITY;
    }

    enum BackButtonProceedPolicy {
        WITH_ACTIVITY,
        SKIP_ACTIVITY
    }
}
