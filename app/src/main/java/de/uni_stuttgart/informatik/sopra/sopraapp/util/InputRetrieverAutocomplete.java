package de.uni_stuttgart.informatik.sopra.sopraapp.util;

import android.content.Context;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InputRetrieverAutocomplete<U> extends InputRetriever {


    private Builder builder;
    AutoCompleteTextView autoCompleteTextView;

    InputRetrieverAutocomplete(Builder<? extends Builder<?, U>, U> builder) {
        super(builder);
        this.builder = builder;
    }

    public static class Builder<T extends Builder<T, U>, U> extends InputRetriever.Builder<Builder<T, U>> {

        Consumer<U> onComplete;
        Collection<U> suggestions;

        public Builder(EditText editText) {
            super(editText);
        }

        public Builder<T, U> withAutocompletion(Collection<U> suggestions) {
            this.suggestions = suggestions;
            return self();
        }

        public Builder<T, U> onSelection(Consumer<U> onSelection) {
            this.onComplete = onSelection;
            return self();
        }

        @Override
        public InputRetrieverAutocomplete<U> build() {
            return new InputRetrieverAutocomplete<>(this);
        }


        @Override
        protected Builder<T, U> self() {
            return this;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void performSomeAction() {
        builder.onComplete.accept(builder.suggestions.stream()
                .filter(o -> o.toString().equals(builder.pressedTextField.getText().toString()))
                .findFirst()
                .orElse(null));
    }

    @Override
    public void show() {
        if (builder.suggestions == null) {
            return;
        }

        EditText pressedTextField = builder.pressedTextField;
        String hint = builder.hint;

        // retrieve context
        Context context = pressedTextField.getContext();

        // sets the layout of the dialog
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View dialogLayout = layoutInflater.inflate(R.layout.activity_bottom_sheet_dialog_interface_autocomplete, null);
        autoCompleteTextView = dialogLayout.findViewById(R.id.userInputDialogAutoComplete);
        autoCompleteTextView.requestFocus();

        autoCompleteTextView.setHint(hint != null ? hint : "");

        // sets text to input field
        autoCompleteTextView.setText(pressedTextField.getText());

        // sets auto complete inputs
        @SuppressWarnings("unchecked") Collection<U> suggestions = builder.suggestions;

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, suggestions.stream().map
                        (Object::toString).collect(Collectors.toList()));
        autoCompleteTextView.setAdapter(adapter);

        // sets courser at the end of the input field
        Selection.setSelection(autoCompleteTextView.getText(), autoCompleteTextView.length());

        showDialog(context, dialogLayout, autoCompleteTextView);
    }

    @Override
    public EditText getTemporaryEditText() {
        return autoCompleteTextView;
    }
}
