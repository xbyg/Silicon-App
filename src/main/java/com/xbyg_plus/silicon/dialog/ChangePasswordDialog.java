package com.xbyg_plus.silicon.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.SchoolAccount;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

public class ChangePasswordDialog extends MyDialog {
    private TextView oldPwdView;
    private EditText newPwdView;
    private EditText newPwdConfirmView;
    private Button submit;

    public ChangePasswordDialog(Context context) {
        super(context);
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_change_pwd, null, false));

        oldPwdView = (TextView) container.findViewById(R.id.old_pwd);
        newPwdView = (EditText) container.findViewById(R.id.new_pwd);
        newPwdConfirmView = (EditText) container.findViewById(R.id.new_pwd_confirm);
        submit = (Button) container.findViewById(R.id.submit);
    }

    public ChangePasswordDialog setContent() {
        SchoolAccountHelper accountHelper = SchoolAccountHelper.getInstance();
        SchoolAccount account = accountHelper.getSchoolAccount();

        String oldPwd = account.getPassword();
        oldPwdView.setText(getContext().getString(R.string.old_pwd, oldPwd.replaceAll(".{" + oldPwd.length() / 3 + "}$", "...")));

        RxView.clicks(submit).subscribe(btn -> {
            if (newPwdView.getText().toString().equals(newPwdConfirmView.getText().toString())) {
                accountHelper.changePassword(newPwdView.getText().toString()).subscribe(
                        () -> showSnackBar(R.string.change_pwd_succeed),
                        throwable -> showSnackBar(throwable.getMessage())
                );
            } else {
                showSnackBar(R.string.change_pwd_confirm_failed);
            }
        });
        return this;
    }
}
