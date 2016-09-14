package org.narcotek.cputemp.app.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import org.narcotek.cputemp.R;

/**
 * Simple about dialog
 */
public final class AboutDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setView(R.layout.dialog_about).setPositiveButton(R.string.dialog_btn_okay, null);

        return builder.create();
    }

}
