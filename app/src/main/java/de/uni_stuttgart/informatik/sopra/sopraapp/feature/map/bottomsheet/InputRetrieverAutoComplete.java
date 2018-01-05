package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.content.Context;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.Collection;
import java.util.stream.Collectors;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;

public class InputRetrieverAutoComplete<T> extends InputRetriever {

    private Collection<T> suggestions;
    private OnPositiveAutocompleteActionCallbackJavaAids<T> onPositiveAutocompleteActionCallbackJavaAids = null;

    /**
     * Constructor
     *
     * @param editText The EditText object whose input should be bound.
     */
    public InputRetrieverAutoComplete(EditText editText) {
        super(editText);
    }

    public InputRetrieverAutoComplete withAutoCompleteSuggestions(Collection<T> suggestions, OnPositiveAutocompleteActionCallbackJavaAids<T> callback) {
        this.suggestions = suggestions;
        this.onPositiveAutocompleteActionCallbackJavaAids = callback;
        return this;
    }

    @Override
    protected void onPositiveAction(String text) {
        if(onPositiveAutocompleteActionCallbackJavaAids != null)
         onPositiveAutocompleteActionCallbackJavaAids.call(
                 suggestions.stream()
                         .filter(o -> o.toString().equals(text))
                         .findFirst()
                         .orElse(null)
         );
    }

    @Override
    public void show() {
        if (suggestions == null) {
            super.show();
            return;
        }

        EditText pressedTextField = getPressedTextField();
        String hint = getHint();

        // retrieve context
        Context context = pressedTextField.getContext();

        // sets the layout of the dialog
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View dialogLayout = layoutInflater.inflate(R.layout.activity_bottom_sheet_dialog_interface_autocomplete, null);
        AutoCompleteTextView autoCompleteTextView = dialogLayout.findViewById(R.id.userInputDialogAutoComplete);
        autoCompleteTextView.requestFocus();

        autoCompleteTextView.setHint(hint != null ? hint : "");

        // sets text to input field
        autoCompleteTextView.setText(pressedTextField.getText());

        // sets auto complete inputs

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, suggestions.stream().map(Object::toString).collect(Collectors.toList()));
        autoCompleteTextView.setAdapter(adapter);

        // sets courser at the end of the input field
        Selection.setSelection(autoCompleteTextView.getText(), autoCompleteTextView.length());

        showDialog(context, dialogLayout, autoCompleteTextView);

    }
}

interface OnPositiveAutocompleteActionCallbackJavaAids<T>{
     void  call(T obj);
}
