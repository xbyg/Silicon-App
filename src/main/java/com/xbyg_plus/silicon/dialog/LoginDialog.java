package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

public class LoginDialog extends Dialog{
    private Completable loginCompletable;

    private EditText stdID;
    private EditText pwd;
    private TextView loginBtn;

    public LoginDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_login);
        SchoolAccountHelper accountHelper = SchoolAccountHelper.getInstance();

        stdID = (EditText) findViewById(R.id.stdID);
        pwd = (EditText) findViewById(R.id.pwd);
        loginBtn = (TextView) findViewById(R.id.loginBtn);

        loginCompletable = Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                RxView.clicks(loginBtn)
                        .doOnNext(btn -> {
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        })
                        .subscribe(btn -> {
                            Completable loginResult = accountHelper.login(stdID.getText().toString(), pwd.getText().toString());
                            loginResult.subscribe(e::onComplete, throwable -> {});
                        });
            }
        });
    }

    public Completable loginCompletable() {
        return loginCompletable;
    }
}
