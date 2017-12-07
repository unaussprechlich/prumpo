package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls;

import android.widget.EditText;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;

public interface FieldValidation {

    default String getIfNotEmptyElseThrow(EditText editText) throws EditFieldValueIsEmptyException {
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
    }
}
