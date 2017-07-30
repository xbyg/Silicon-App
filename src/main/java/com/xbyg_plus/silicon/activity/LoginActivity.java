package com.xbyg_plus.silicon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper.LoginCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.stdID) EditText stdID;
    @BindView(R.id.pwd) EditText pwd;
    @BindView(R.id.autoLogin) CheckBox autoLogin;
    @BindView(R.id.loginBtn) Button loginBtn;
    @BindView(R.id.guest_mode) TextView guestMode;

    private SchoolAccountHelper accountHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        DialogManager.provideContext(this);

        accountHelper = SchoolAccountHelper.getInstance();
        accountHelper.tryAutoLogin(result -> {
            if (result == LoginCallback.LOGIN_SUCCEEDED) {
                startMainActivity();
            }
        });

        guestMode.setOnClickListener(v -> {
            startMainActivity();
        });

        loginBtn.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            accountHelper.login(stdID.getText().toString(), pwd.getText().toString(), result -> {
                if (result == LoginCallback.LOGIN_SUCCEEDED) {
                    if (!accountHelper.isAutoLogin() && autoLogin.isChecked()) {
                        accountHelper.enableAutoLogin(stdID.getText().toString(), pwd.getText().toString());
                    }
                    startMainActivity();
                }
            });
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }
}
