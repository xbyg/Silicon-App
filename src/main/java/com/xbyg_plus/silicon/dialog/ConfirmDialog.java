package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.util.List;

public class ConfirmDialog extends Dialog {
    private TextView titleView;
    private TextView messageView;
    private Button okBtn;
    private Button cancelBtn;

    public interface ConfirmCallback {
        void onConfirmed(boolean confirmation);
    }

    protected ConfirmDialog(Context context) {
        super(context);
        LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null, false);
        titleView = (TextView) root.findViewById(R.id.title);
        messageView = (TextView) root.findViewById(R.id.message);
        okBtn = (Button) root.findViewById(R.id.ok);
        cancelBtn = (Button) root.findViewById(R.id.cancel);

        setContentView(root);
    }

    public ConfirmDialog setConfirmCallback(ConfirmCallback callback) {
        if (callback != null) {
            okBtn.setOnClickListener(v -> {
                dismiss();
                callback.onConfirmed(true);
            });
            cancelBtn.setOnClickListener(v -> {
                dismiss();
                callback.onConfirmed(false);
            });
        }
        return this;
    }

    public ConfirmDialog setContent(String text, String message) {
        titleView.setText(text);
        messageView.setText(message);
        return this;
    }

    public ConfirmDialog setContent(List<WebResourceInfo> infoList) {
        String nameList = "";
        for (WebResourceInfo info : infoList) {
            nameList += info.getName() + "\n";
        }
        return this.setContent(getContext().getString(R.string.download_files_confirm), nameList);
    }
}
