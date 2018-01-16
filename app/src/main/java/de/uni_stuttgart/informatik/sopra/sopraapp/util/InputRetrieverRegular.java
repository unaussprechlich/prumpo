package de.uni_stuttgart.informatik.sopra.sopraapp.util;

import android.content.Context;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

public class InputRetrieverRegular extends InputRetriever {

    private Builder builder;

    InputRetrieverRegular(Builder builder) {
        super(builder);
        this.builder = builder;
    }

    public static class Builder extends InputRetriever.Builder<Builder> {

        public Builder(EditText editText) {
            super(editText);
        }

        @Override
        public InputRetrieverRegular build() {
            return new InputRetrieverRegular(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    @Override
    public void show() {
        // retrieve context
        Context context = builder.pressedTextField.getContext();

        // sets the layout of the dialog
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View dialogLayout = layoutInflater.inflate(R.layout.activity_bottom_sheet_dialog_interface, null);

        // find the input field of the dialog
        EditText editText = dialogLayout.findViewById(R.id.userInputDialog);
        editText.requestFocus();

        // sets hint to input field
        editText.setHint(builder.hint != null ? builder.hint : "");

        // sets text to input field
        editText.setText(builder.pressedTextField.getText());

        // sets courser at the end of the input field
        Selection.setSelection(editText.getText(), editText.length());

        showDialog(context, dialogLayout, editText);
    }


}
