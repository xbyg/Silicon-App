package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper.LoginCallback;

public class LoginDialog extends Dialog{
    private SchoolAccountHelper accountHelper;

    private EditText stdID;
    private EditText pwd;
    private TextView loginBtn;

    public LoginDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_login);
        accountHelper = SchoolAccountHelper.getInstance();

        stdID = (EditText) findViewById(R.id.stdID);
        pwd = (EditText) findViewById(R.id.pwd);
        loginBtn = (TextView) findViewById(R.id.loginBtn);
    }

    public LoginDialog setLoginCallback(LoginCallback callback) {
        loginBtn.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            accountHelper.login(stdID.getText().toString(), pwd.getText().toString(), callback);
        });
        return this;
    }
}
