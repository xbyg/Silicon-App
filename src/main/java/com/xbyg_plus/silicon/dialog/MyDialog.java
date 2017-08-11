package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;

public abstract class MyDialog extends Dialog {
    protected View container;

    public MyDialog(Context context) {
        super(context);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        this.container = view;
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(LayoutInflater.from(getContext()).inflate(layoutResID, null, false));
    }

    public void showSnackBar(int msgId) {
        showSnackBar(getContext().getString(msgId));
    }

    public void showSnackBar(String msg) {
        Snackbar.make(container, msg, Snackbar.LENGTH_LONG).show();
    }
}
