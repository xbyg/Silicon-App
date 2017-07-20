package com.xbyg_plus.silicon.dialog;

import android.app.AlertDialog;
import android.content.Context;

import com.xbyg_plus.silicon.callback.ConfirmCallback;

public class ConfirmDialog {
    public ConfirmDialog(Context context, String title, String msg, ConfirmCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("YES", (dialog, which) -> {
                    dialog.dismiss();
                    callback.onConfirmed(true);
                })
                .setNegativeButton("NO", (dialog, which) -> {
                    dialog.dismiss();
                    callback.onConfirmed(false);
                });
        builder.create().show();
    }
}
