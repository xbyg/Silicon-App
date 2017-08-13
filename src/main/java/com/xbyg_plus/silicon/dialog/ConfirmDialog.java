package com.xbyg_plus.silicon.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;

public class ConfirmDialog extends MyDialog {
    private TextView titleView;
    private TextView messageView;
    private Button okBtn;
    private Button cancelBtn;

    protected ConfirmDialog(Context context) {
        super(context);
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null, false));

        titleView = (TextView) container.findViewById(R.id.title);
        messageView = (TextView) container.findViewById(R.id.message);
        okBtn = (Button) container.findViewById(R.id.ok);
        cancelBtn = (Button) container.findViewById(R.id.cancel);
    }

    public Single<Boolean> confirmObservable() {
        return Single.create((SingleEmitter<Boolean> e) -> {
            okBtn.setOnClickListener(v -> e.onSuccess(true));
            cancelBtn.setOnClickListener(v -> e.onSuccess(false));
        }).doOnSuccess(b -> this.dismiss());
    }

    public ConfirmDialog setContent(String text, String message) {
        titleView.setText(text);
        messageView.setText(message);
        return this;
    }
}
