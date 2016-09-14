package org.narcotek.cputemp.app.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.LinearLayout;

import org.narcotek.cputemp.R;

/**
 * Simple dialog using a WebView to show licenses
 */
public final class LicensesDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_licenses, null);

        WebView webView = (WebView) linearLayout.findViewById(R.id.dialog_licenses_wv);
        webView.loadUrl("file:///android_asset/licenses.html");

        Builder builder = new Builder(getActivity());
        builder.setView(linearLayout).setPositiveButton(R.string.dialog_btn_okay, null);

        return builder.create();
    }

}
