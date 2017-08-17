package com.xbyg_plus.silicon.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.utils.functions.Consumer;

public class ConfirmDialog extends MyDialog {
    private TextView titleView;
    private TextView messageView;
    private Button okBtn;
    private Button cancelBtn;

    public ConfirmDialog(Context context) {
        super(context);
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null, false));

        titleView = (TextView) container.findViewById(R.id.title);
        messageView = (TextView) container.findViewById(R.id.message);
        okBtn = (Button) container.findViewById(R.id.ok);
        cancelBtn = (Button) container.findViewById(R.id.cancel);
    }

    public ConfirmDialog setOnConfirmConsumer(Consumer<Boolean> consumer) {
        okBtn.setOnClickListener(btn -> {
            consumer.accept(true);
            this.dismiss();
        });
        cancelBtn.setOnClickListener(btn -> {
            consumer.accept(false);
            this.dismiss();
        });
        return this;
    }

    public ConfirmDialog setContent(String text, String message) {
        titleView.setText(text);
        messageView.setText(message);
        return this;
    }
}
