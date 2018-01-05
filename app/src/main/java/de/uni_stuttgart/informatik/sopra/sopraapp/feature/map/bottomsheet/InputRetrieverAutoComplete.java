package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.content.Context;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

import java.util.Collection;

public class InputRetrieverAutoComplete extends InputRetriever {

    private Collection<String> suggestions;

    /**
     * Constructor
     *
     * @param editText The EditText object whose input should be bound.
     */
    private InputRetrieverAutoComplete(EditText editText) {
        super(editText);
    }

    public InputRetrieverAutoComplete withAutoCompleteSuggestions(Collection<String> suggestions) {
        this.suggestions = suggestions;
        return this;
    }

    public static InputRetrieverAutoComplete of(EditText editText){
        return new InputRetrieverAutoComplete(editText);
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
        String[] acSuggestions = suggestions.toArray(new String[suggestions.size()]);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, acSuggestions);
        autoCompleteTextView.setAdapter(adapter);

        // sets courser at the end of the input field
        Selection.setSelection(autoCompleteTextView.getText(), autoCompleteTextView.length());

        showDialog(context, dialogLayout, autoCompleteTextView);

    }
}
