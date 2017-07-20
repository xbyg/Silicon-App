package com.xbyg_plus.silicon.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.callback.ChangePasswordCallback;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

public class ChangePasswordDialog {
    public ChangePasswordDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_change_pwd, null, false);
        TextView old_pwd = (TextView) root.findViewById(R.id.old_pwd);
        EditText new_pwd = (EditText) root.findViewById(R.id.new_pwd);
        EditText new_pwd_confirm = (EditText) root.findViewById(R.id.new_pwd_confirm);
        Button submit = (Button) root.findViewById(R.id.submit);

        String oldPwd = SchoolAccountHelper.getInstance().getSchoolAccount().getPassword();
        old_pwd.setText(context.getString(R.string.old_pwd) + ": " + oldPwd.replaceAll(".{" + oldPwd.length() / 2 + "}$", "..."));
        submit.setOnClickListener(v -> {
            if (new_pwd.getText().toString().equals(new_pwd_confirm.getText().toString())) {
                SchoolAccountHelper.getInstance().changePassword(new_pwd.getText().toString(), new ChangePasswordCallback() {
                    @Override
                    public void onFailed(int reason) {
                        switch (reason) {
                            case FAILED_ILLEGAL_PWD:
                                Snackbar.make(root, context.getString(R.string.change_pwd_illegal_pwd), Snackbar.LENGTH_LONG).show();
                                break;
                            case FAILED_SAME_PWD:
                                Snackbar.make(root, context.getString(R.string.change_pwd_same_pwd), Snackbar.LENGTH_LONG).show();
                                break;
                            case FAILED_IO_EXCEPTION:
                                Snackbar.make(root, context.getString(R.string.change_pwd_io_exception), Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onPasswordChanged() {
                        Snackbar.make(root, context.getString(R.string.change_pwd_succeed), Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                Snackbar.make(root, context.getString(R.string.change_pwd_confirm_failed), Snackbar.LENGTH_LONG).show();
            }
        });

        builder.setView(root).create().show();
    }
}
