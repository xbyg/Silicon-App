package com.xbyg_plus.silicon.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.view.View;

public class LoadingDialog extends ProgressDialog {
    private View container;

    protected LoadingDialog(Activity activity) {
        super(activity);
        this.setCancelable(true);
        this.setIndeterminate(true);
        this.setDismissMessageContainer(activity.findViewById(android.R.id.content));
    }

    public void setTitleAndMessage(String title, String msg) {
        //this dialog is usually used in OkHttp thread,we need to set the title and message in ui thread
        new Handler(Looper.getMainLooper()).post(() -> {
            setTitle(title);
            setMessage(msg);
        });
    }

    public void setDismissMessageContainer(View container) {
        if (container != null) {
            this.container = container;
        }
    }

    public void dismiss(String dismissMessage) {
        this.dismiss();
        Snackbar.make(container, dismissMessage, Snackbar.LENGTH_LONG).show();
    }
}
