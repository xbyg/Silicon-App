package com.xbyg_plus.silicon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.xbyg_plus.silicon.callback.LoginCallback;
import com.xbyg_plus.silicon.dialog.LoadingDialog;
import com.xbyg_plus.silicon.utils.CachesDatabase;
import com.xbyg_plus.silicon.utils.DownloadsDatabase;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity{
    @BindView(R.id.stdID) EditText stdID;
    @BindView(R.id.pwd) EditText pwd;
    @BindView(R.id.autoLogin) CheckBox autoLogin;
    @BindView(R.id.loginBtn) Button loginBtn;

    private LoadingDialog loadingDialog;

    private SchoolAccountHelper accountHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Logger.addLogAdapter(new AndroidLogAdapter());
        CachesDatabase.init(this);
        DownloadsDatabase.init(this);
        ButterKnife.bind(this);
        OKHTTPClient.init();
        accountHelper = new SchoolAccountHelper(this);

        loadingDialog = new LoadingDialog(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.contains("id")){
            loadingDialog.show();
            accountHelper.login(preferences.getString("id",""),preferences.getString("pwd",""),callback);
        }

        loginBtn.setOnClickListener(v->{
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            loadingDialog.show();
            accountHelper.login(stdID.getText().toString(),pwd.getText().toString(),callback);
        });
    }

    private LoginCallback callback = new LoginCallback(){
        public void onLoadEncryptionFile() {
            loadingDialog.setTitleAndMessage(getString(R.string.encryption), getString(R.string.loading_md5_js));
        }

        public void onLoadEncryptionFileFailed() {
            loadingDialog.dismiss(getString(R.string.cannot_load_encryption_js));
        }

        public void onEncryptPassword() {
            loadingDialog.setTitleAndMessage(getString(R.string.encryption), getString(R.string.encrypting_pwd));
        }

        public void onEncryptPasswordFailed() {
            loadingDialog.dismiss(getString(R.string.cannot_encrypt_pwd));
        }

        public void onRequestLogin() {
            loadingDialog.setTitleAndMessage(getString(R.string.network), getString(R.string.requesting) + " http://58.177.253.171/it-school/php/login_do.php3");
        }

        public void onRequestLoginSucceeded() {
            loadingDialog.dismiss();
            if (!preferences.contains("id") && autoLogin.isChecked()) {
                preferences.edit().putString("id", stdID.getText().toString()).putString("pwd", pwd.getText().toString()).apply();
            }
            finish();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        public void onRequestLoginFailed(int reason) {
            String msg = "";
            switch (reason){
                case LoginCallback.LOGIN_FAILED_DATA_WRONG:
                    msg = getString(R.string.login_data_wrong);
                    break;
                case LoginCallback.LOGIN_FAILED_IO_EXCEPTION:
                    msg = getString(R.string.login_io_exception);
                    break;
                case LoginCallback.LOGIN_FAILED_CANNOT_INIT_AC:
                    msg = getString(R.string.login_ac_init);
                    break;
            }
            loadingDialog.dismiss(msg);
        }
    };
}
