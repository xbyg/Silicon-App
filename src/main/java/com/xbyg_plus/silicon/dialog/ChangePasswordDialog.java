package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.SchoolAccount;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper.ChangePasswordCallback;

public class ChangePasswordDialog extends Dialog {
    private View container;
    private TextView oldPwdView;
    private EditText newPwdView;
    private EditText newPwdConfirmView;
    private Button submit;

    protected ChangePasswordDialog(Context context) {
        super(context);
        container = LayoutInflater.from(context).inflate(R.layout.dialog_change_pwd, null, false);
        oldPwdView = (TextView) container.findViewById(R.id.old_pwd);
        newPwdView = (EditText) container.findViewById(R.id.new_pwd);
        newPwdConfirmView = (EditText) container.findViewById(R.id.new_pwd_confirm);
        submit = (Button) container.findViewById(R.id.submit);

        setContentView(container);
    }

    public ChangePasswordDialog setContent(SchoolAccount account) {
        String oldPwd = account.getPassword();
        oldPwdView.setText(getContext().getString(R.string.old_pwd) + ": " + oldPwd.replaceAll(".{" + oldPwd.length() / 2 + "}$", "..."));
        submit.setOnClickListener(v -> {
            if (newPwdView.getText().toString().equals(newPwdConfirmView.getText().toString())) {
                SchoolAccountHelper.getInstance().changePassword(newPwdView.getText().toString(), result -> {
                    String msg = "";
                    switch (result) {
                        case ChangePasswordCallback.SUCCEED:
                            msg = getContext().getString(R.string.change_pwd_succeed);
                            break;
                        case ChangePasswordCallback.FAILED_ILLEGAL_PWD:
                            msg = getContext().getString(R.string.change_pwd_illegal_pwd);
                            break;
                        case ChangePasswordCallback.FAILED_SAME_PWD:
                            msg = getContext().getString(R.string.change_pwd_same_pwd);
                            break;
                        case ChangePasswordCallback.FAILED_IO_EXCEPTION:
                            msg = getContext().getString(R.string.io_exception);
                            break;
                    }
                    Snackbar.make(container, msg, Snackbar.LENGTH_LONG).show();
                });
            } else {
                Snackbar.make(container, getContext().getString(R.string.change_pwd_confirm_failed), Snackbar.LENGTH_LONG).show();
            }
        });
        return this;
    }
}
