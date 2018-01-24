package de.uni_stuttgart.informatik.sopra.sopraapp.app;

import android.widget.EditText;

import dagger.android.support.DaggerAppCompatActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;


public abstract class BaseActivity extends DaggerAppCompatActivity {

    protected static String getFieldValueIfNotEmpty(EditText editText) throws EditFieldValueIsEmptyException {
        editText.setError(null);
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
    }

    protected static void resetEditText(EditText editText){
        editText.setError(null);
        editText.setText("");
    }
}
