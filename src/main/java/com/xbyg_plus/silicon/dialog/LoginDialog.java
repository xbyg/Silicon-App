package com.xbyg_plus.silicon.dialog;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

import io.reactivex.Completable;

public class LoginDialog extends MyDialog {
    private EditText stdID;
    private EditText pwd;
    private TextView loginBtn;

    public LoginDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_login);

        stdID = (EditText) findViewById(R.id.stdID);
        pwd = (EditText) findViewById(R.id.pwd);
        loginBtn = (TextView) findViewById(R.id.loginBtn);
    }

    public LoginDialog setLoginAction(Runnable action) {
        RxView.clicks(loginBtn)
                .doOnNext(btn -> {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                })
                .subscribe(btn -> {
                    Completable loginResult = SchoolAccountHelper.getInstance().login(stdID.getText().toString(), pwd.getText().toString());
                    loginResult.doOnComplete(this::dismiss)
                            .subscribe(action::run, throwable -> showSnackBar(R.string.io_exception));
                });
        return this;
    }
}
