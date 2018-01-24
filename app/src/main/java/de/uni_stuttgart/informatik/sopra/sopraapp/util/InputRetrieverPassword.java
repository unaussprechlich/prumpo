package de.uni_stuttgart.informatik.sopra.sopraapp.util;

import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.widget.EditText;

public class InputRetrieverPassword extends InputRetrieverRegular {


    InputRetrieverPassword(InputRetrieverRegular.Builder builder) {
        super(builder);
    }

    public static class Builder extends InputRetrieverRegular.Builder {
        public Builder(EditText editText) {
            super(editText);
        }

        @Override
        public InputRetrieverPassword build() {
            return new InputRetrieverPassword(this);
        }
    }


    @Override
    protected TransformationMethod getTransformationMethod() {
        return PasswordTransformationMethod.getInstance();
    }
}
