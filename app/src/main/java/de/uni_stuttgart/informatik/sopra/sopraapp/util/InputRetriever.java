package de.uni_stuttgart.informatik.sopra.sopraapp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import butterknife.BindString;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;

public abstract class InputRetriever {

    @BindString(R.string.map_frag_botsheet_dialog_default_header)
    String defaultTitle;

    @BindString(R.string.map_frag_botsheet_dialog_positive)
    String dialogAccept;

    @BindString(R.string.map_frag_botsheet_dialog_negative)
    String dialogReject;

    private Builder<?> builder;

    InputRetriever(Builder<? extends Builder> builder) {
        this.builder = builder;
        ButterKnife.bind(this, builder.pressedTextField);
    }

    void showDialog(Context context, View dialogLayout, EditText editText) {

        AlertDialog alertDialog = new FixedDialog(context)
                .setView(dialogLayout)
                .setCancelable(false)
                .setTitle(builder.title != null ? builder.title : defaultTitle)
                .setPositiveButton(dialogAccept, (dialogInterface, i) -> {
                            builder.pressedTextField.setText(editText.getText());
                            performSomeAction();
                            if (builder.positiveAction != null)
                                builder.positiveAction.onClick(dialogInterface, i);
                        }
                )
                .setNegativeButton(dialogReject, (dialogInterface, i) -> {
                    if (builder.negativeAction != null)
                        builder.negativeAction.onClick(dialogInterface, i);
                })
                .create();

        Window window = alertDialog.getWindow();
        if (window != null)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alertDialog.show();
    }

    public abstract void show();

    /**
     * Is only available after show() was invoked.
     * @return the editText field of the input dialog
     */
    public abstract EditText getTemporaryEditText();

    public abstract static class Builder<T extends Builder<T>> {

        DialogInterface.OnClickListener positiveAction;
        DialogInterface.OnClickListener negativeAction;

        String title;
        String hint;

        EditText pressedTextField;

        public Builder(EditText editText) {
            this.pressedTextField = editText;
        }

        public T withTitle(String title) {
            this.title = title;
            return self();
        }

        public T withHint(String hint) {
            this.hint = hint;
            return self();
        }

        public T setPositiveButtonAction(DialogInterface.OnClickListener positiveAction) {
            this.positiveAction = positiveAction;
            return self();
        }

        public T setNegativeButtonAction(DialogInterface.OnClickListener negativeAction) {
            this.negativeAction = negativeAction;
            return self();
        }

        public abstract InputRetriever build();

        protected abstract T self();
    }

    public void performSomeAction(){

    }

    public static InputRetrieverRegular.Builder newRegularInputRetrieverFrom(EditText editText) {
        return new InputRetrieverRegular.Builder(editText);
    }

    public static InputRetrieverAutocomplete.Builder newInputRetrieverAutoCompleteFrom(EditText editText) {
        return new InputRetrieverAutocomplete.Builder(editText);
    }
}
