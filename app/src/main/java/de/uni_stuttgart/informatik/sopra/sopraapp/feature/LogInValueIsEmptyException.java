package de.uni_stuttgart.informatik.sopra.sopraapp.feature;

import android.widget.EditText;

public class LogInValueIsEmptyException extends LogInValueException {
    public LogInValueIsEmptyException(EditText editText) {
        super(editText, "Field is empty!");
    }
}
