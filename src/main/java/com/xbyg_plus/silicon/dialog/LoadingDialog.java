package com.xbyg_plus.silicon.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class LoadingDialog extends ProgressDialog {
    private View activityRootViewGroup;

    public LoadingDialog(Context context) {
        super(context);
        this.setCancelable(false);
        this.setIndeterminate(true);
        this.activityRootViewGroup = (((Activity) context).findViewById(android.R.id.content));
    }

    public LoadingDialog setMessage(int stringId) {
        return this.setMessage(getContext().getString(stringId));
    }

    public LoadingDialog setMessage(String msg) {
        //this dialog is usually used in non main thread, we need to set the message in ui thread
        Observable.just(msg).observeOn(AndroidSchedulers.mainThread()).subscribe(super::setMessage);
        return this;
    }

    public void dismiss(int dismissMessageId) {
        this.dismiss(getContext().getString(dismissMessageId));
    }

    public void dismiss(String dismissMessage) {
        this.dismiss();
        Snackbar.make(activityRootViewGroup, dismissMessage, Snackbar.LENGTH_LONG).show();
    }

    public void dismiss(int dismissMessageId, View v) {
        this.dismiss(getContext().getString(dismissMessageId), v);
    }

    public void dismiss(String dismissMessage, View v) {
        this.dismiss();
        Snackbar.make(v, dismissMessage, Snackbar.LENGTH_LONG).show();
    }
}
