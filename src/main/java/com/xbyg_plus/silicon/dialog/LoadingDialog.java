package com.xbyg_plus.silicon.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;

public class LoadingDialog {
    private ProgressDialog dialog;
    private Activity activity;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
        dialog.setCancelable(true);
        dialog.setIndeterminate(true);
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public void setTitleAndMessage(String title, String msg) {
        activity.runOnUiThread(() -> {
            dialog.setTitle(title);
            dialog.setMessage(msg);
        });
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void dismiss(String dismissMessage) {
        dialog.dismiss();
        dialog.setOnDismissListener(dialog -> Snackbar.make(activity.findViewById(android.R.id.content), dismissMessage, Snackbar.LENGTH_LONG).show());
    }
}
