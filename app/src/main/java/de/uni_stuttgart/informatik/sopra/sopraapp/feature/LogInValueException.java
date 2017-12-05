package de.uni_stuttgart.informatik.sopra.sopraapp.feature;

import android.os.Vibrator;
import android.widget.EditText;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;

public class LogInValueException extends Exception {

    final EditText editText;

    @Inject
    Vibrator vibrator;

    public LogInValueException(EditText editText, String message) {
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
