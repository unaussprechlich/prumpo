package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class FixedDialog extends AlertDialog.Builder {

    public FixedDialog(Context arg0) {
        super(arg0);
    }

    @Override
    public AlertDialog.Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        return super.setNegativeButton(text, listener);

    }

    @Override
    public AlertDialog.Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        return super.setPositiveButton(text, listener);
    }

}
