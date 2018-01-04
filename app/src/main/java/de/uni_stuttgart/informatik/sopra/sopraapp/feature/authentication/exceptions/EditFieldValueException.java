package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions;

import android.os.Vibrator;
import android.widget.EditText;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;

public class EditFieldValueException extends Exception {

    private final EditText editText;

    @Inject
    Vibrator vibrator;

    public EditFieldValueException(EditText editText, String message) {
        super(message);
        this.editText = editText;
        SopraApp.getAppComponent().inject(this);

    }

    public void showError() {
        editText.setError(getMessage());
        editText.requestFocus();
        vibrator.vibrate(500);
    }
}
