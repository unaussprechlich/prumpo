package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions;

import android.widget.EditText;

public class EditFieldValueIsEmptyException extends EditFieldValueException {
    public EditFieldValueIsEmptyException(EditText editText) {
        super(editText, "Field is empty!");
    }
}
