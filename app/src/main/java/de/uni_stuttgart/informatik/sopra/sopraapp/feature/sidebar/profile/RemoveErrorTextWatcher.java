package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.text.TextWatcher;

public interface RemoveErrorTextWatcher extends TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after) { /*Ignore*/ }

    @Override
    default void onTextChanged(CharSequence s, int start, int before, int count) { /*Ignore*/ }

}
