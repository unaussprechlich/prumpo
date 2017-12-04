package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;

/**
 * Used for creating an input dialog which
 * automatically sets the text to the bound EditText.
 * Implements the Builder pattern.
 */
@SuppressWarnings("unused")
public class InputRetriever implements View.OnClickListener {

    /**
     * Holder for the positive action: When user selects "OK".
     */
    private DialogInterface.OnClickListener positiveAction;

    /**
     * Holder for the negative action: When user selects "Cancel".
     */
    private DialogInterface.OnClickListener negativeAction;

    /**
     * A place holder for bound EditText.
     * Set by constructor.
     */
    private EditText pressedTextField;

    /**
     * Holder for the dialog header.
     */
    private String title;

    /**
     * Holder for the hint of the dialog input field.
     */
    private String hint;

    /**
     * Constructor
     *
     * @param editText The EditText object whose input should be bound.
     */
    private InputRetriever(EditText editText) {
        editText.setClickable(true);
        this.pressedTextField = editText;
    }

    /**
     * Method to create a Dialog with the builder pattern
     * which is bound to the {@code editText} parameter.
     *
     * @param editText the EditText object to bound.
     * @return the InputRetriever
     */
    public static InputRetriever of(EditText editText) {
        return new InputRetriever(editText);
    }

    /**
     * Returns the input of the dialog at the time the user hits "OK".
     *
     * @return the result after user hitted "OK".
     */
    public Editable retrieveResult() {
        return pressedTextField.getText();
    }

    /**
     * Sets the text of the "OK" button.
     *
     * @param positiveAction the action to invoke when "OK" is pressed.
     * @return a reference to it self
     */
    public InputRetriever setPositiveButton(DialogInterface.OnClickListener positiveAction) {
        this.positiveAction = positiveAction;
        return this;
    }

    /**
     * Sets the text of the "Cancel" button.
     *
     * @param negativeAction the action to invoke when "Cancel" is pressed.
     * @return a reference to it self
     */
    public InputRetriever setNegativeButton(DialogInterface.OnClickListener negativeAction) {
        this.negativeAction = negativeAction;
        return this;
    }

    /**
     * Sets the text of the input dialog header.
     *
     * @param title the new header text
     * @return a reference to it self
     */
    public InputRetriever withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the text of the input dialog header.
     *
     * @param title a reference to the R.string - value string.
     * @return a reference to it self
     */
    public InputRetriever withTitle(int title) {
        this.title = getString(title);
        return this;
    }

    /**
     * Sets the text of the input dialog hint.
     * Only displayed when field is empty.
     *
     * @param hint the new header text
     * @return a reference to it self
     */
    public InputRetriever withHint(String hint) {
        this.hint = hint;
        return this;
    }

    /**
     * Sets the text of the input dialog hint.
     * Only displayed when field is empty.
     *
     * @param hint a reference to the R.string - value string.
     * @return a reference to it self
     */
    public InputRetriever withHint(int hint) {
        this.hint = getString(hint);
        return this;
    }

    /**
     * Private method to retrieve a string from the R.string resource folder.
     * The bound text field will be used to get the reference.
     *
     * @param id the R.string id for the resource
     * @return the string found with the id
     */
    private String getString(int id) {
        return pressedTextField.getResources().getString(id);
    }

    @Override
    public void onClick(View view) {

        // retrieve context
        Context context = view.getContext();

        // sets the layout of the dialog
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View dialogLayout = layoutInflater.inflate(R.layout.activity_bottom_sheet_dialog_interface, null);

        // find the input field of the dialog
        EditText editText = dialogLayout.findViewById(R.id.userInputDialog);

        // sets hint to input field
        editText.setHint(hint != null ? hint : "");

        // sets text to input field
        editText.setText(pressedTextField.getText());

        // sets courser at the end of the input field
        int lastChar = editText.length();
        Editable editable = editText.getText();
        Selection.setSelection(editable, lastChar);

        // create the alert an show
        new AlertDialog.Builder(context)
                .setView(dialogLayout)
                .setCancelable(false)
                .setTitle(title == null
                        ? getString(R.string.map_frag_botsheet_dialog_default_header)
                        : title)
                .setPositiveButton(getString(R.string.map_frag_botsheet_alert_yes),
                        positiveAction != null ? positiveAction : (a, b) ->
                                pressedTextField.setText(editText.getText()))
                .setNegativeButton(getString(R.string.map_frag_botsheet_alert_no),
                        negativeAction != null ? negativeAction : (a, b) -> {
                        })
                .create()
                .show();
    }
}
