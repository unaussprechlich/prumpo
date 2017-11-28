package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar;


public interface OnBackPressedListener {

    /**
     * Only called if {@link OnBackPressedListener#requestBackButtonControl()} returns true
     */
    void onBackPressed();

    /**
     * Tells if back button should be controlled by fragment
     *
     * @return true if fragments wants controll over back button
     */
    boolean requestBackButtonControl();

}
