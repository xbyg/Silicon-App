package com.xbyg_plus.silicon.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.dialog.LoadingDialog;
import com.xbyg_plus.silicon.model.SchoolAccount;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SchoolAccountHelper implements DialogManager.DialogHolder {
    private static SchoolAccountHelper instance;

    private Context context;
    private SharedPreferences preferences;

    private LoadingDialog loadingDialog;

    private SchoolAccount schoolAccount;
    private boolean isGuestMode = true;
    private boolean isAutoLogin = false;
    private boolean isLoggedIn = false;

    public SchoolAccountHelper(Context context) {
        instance = this;
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.isAutoLogin = preferences.contains("id");
        DialogManager.registerDialogHolder(this);
    }

    public static SchoolAccountHelper getInstance() {
        return instance;
    }

    @Override
    public void requestDialogs(DialogManager dialogManager) {
        this.loadingDialog = dialogManager.obtain(LoadingDialog.class);
    }

    @Override
    public void releaseDialogs() {
        this.loadingDialog = null;
    }

    public interface LoginCallback {
        int LOGIN_SUCCEEDED = 0;
        int LOGIN_FAILED_USER_DATA_WRONG = 1;
        int LOGIN_FAILED_IO_EXCEPTION = 2;
        int LOGIN_FAILED_CANNOT_LOAD_ENCRYPTION_JS = 3;
        int LOGIN_FAILED_CANNOT_ENCRYPT_PASSWORD = 4;

        void onResult(int result);
    }

    public void tryAutoLogin(LoginCallback callback) {
        if (isAutoLogin) {
            login(preferences.getString("id", ""), preferences.getString("pwd", ""), callback);
        }
    }

    public void enableAutoLogin(String id, String pwd) {
        isAutoLogin = true;
        preferences.edit().putString("id", id).putString("pwd", pwd).apply();
    }

    public void disableAutoLogin() {
        isAutoLogin = false;
        preferences.edit().remove("id").remove("pwd").apply();
    }

    /**
     * @see #loadMD5JS()
     * @see #encryptPwd(String, String, JsCallback)
     * This function loads MD5.js and encrypt the user password
     * Then post the login data to the server and initialize an school account.
     * @see SchoolAccount
     */
    public void login(final String id, final String pwd, final LoginCallback callback) {
        loadingDialog.setCancelable(false);

        try {
            loadingDialog.setTitleAndMessage(context.getString(R.string.encryption), context.getString(R.string.loading_md5_js));
            loadingDialog.show();
            String script = loadMD5JS();

            loadingDialog.setTitleAndMessage(context.getString(R.string.encryption), context.getString(R.string.encrypting_pwd));
            encryptPwd(script, pwd, new JsCallback() {
                @Override
                public void onResult(String encrypted_pwd) {
                    loadingDialog.setTitleAndMessage(context.getString(R.string.network), context.getString(R.string.requesting, " http://58.177.253.171/it-school/php/login_do.php3"));
                    Map<String, String> postData = new HashMap<>();
                    postData.put("userloginid", id);
                    postData.put("password", encrypted_pwd);
                    //FakePassword and language fields can be ignored
                    OKHTTPClient.post("http://58.177.253.171/it-school/php/login_do.php3", postData, new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.body().string().contains("main.php3")) {
                                isGuestMode = false;
                                isLoggedIn = true;
                                initSchoolAccount(id, pwd, callback);
                            } else {
                                loadingDialog.dismiss(context.getString(R.string.login_data_wrong));
                                callback.onResult(LoginCallback.LOGIN_FAILED_USER_DATA_WRONG);
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            loadingDialog.dismiss(context.getString(R.string.login_io_exception));
                            callback.onResult(LoginCallback.LOGIN_FAILED_IO_EXCEPTION);
                        }
                    });
                }

                @Override
                public void onError(String s) {
                    loadingDialog.dismiss(context.getString(R.string.cannot_encrypt_pwd));
                    callback.onResult(LoginCallback.LOGIN_FAILED_CANNOT_ENCRYPT_PASSWORD);
                }
            });
        } catch (IOException e) {
            loadingDialog.dismiss(context.getString(R.string.cannot_load_encryption_js));
            callback.onResult(LoginCallback.LOGIN_FAILED_CANNOT_LOAD_ENCRYPTION_JS);
        }
    }

    /**
     * This md5.js is a copy of the javascript of the following link
     *
     * @link http://58.177.253.171/it-school//js/md5.js
     */
    private String loadMD5JS() throws IOException {
        InputStream stream = context.getResources().openRawResource(R.raw.md5);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String context;
        while ((context = reader.readLine()) != null) {
            builder.append(context);
        }
        return builder.toString();
    }

    /**
     * The js-evaluator has to run on ui thread.
     * It calls the MD5 function defined in the md5.js
     *
     * @link https://github.com/evgenyneu/js-evaluator-for-android
     */
    private void encryptPwd(String script, String pwd, JsCallback callback) {
        new Handler(Looper.getMainLooper()).post(() -> new JsEvaluator(context).callFunction(script, callback, "MD5", pwd));
    }

    /**
     * It send a request to http://58.177.253.171/it-school/php/home_v5.php3
     * then parsing the response to get student's name,class room and class number
     */
    private void initSchoolAccount(final String id, final String pwd, final LoginCallback callback) {
        if (schoolAccount == null && isLoggedIn) {
            OKHTTPClient.get("http://58.177.253.171/it-school/php/home_v5.php3", new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Document doc = Jsoup.parse(response.body().string());
                    String welcome_msg = doc.select("div.right_content h1").first().text();
                    String name = getMatch(welcome_msg, "(?<=\\)\\ ).*(?=\\　)");
                    String classRoom = getMatch(welcome_msg, "(?<=\\()[1-6][A-F]");
                    int classNo = Integer.parseInt(getMatch(welcome_msg, "[0-9]{1,2}(?=\\))"));

                    schoolAccount = new SchoolAccount(name, classRoom, classNo, id, pwd);
                    loadingDialog.dismiss();
                    callback.onResult(LoginCallback.LOGIN_SUCCEEDED);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    loadingDialog.dismiss(context.getString(R.string.login_io_exception));
                    callback.onResult(LoginCallback.LOGIN_FAILED_IO_EXCEPTION);
                }
            });
        }
    }

    private String getMatch(String string, String pattern) {
        Matcher m = Pattern.compile(pattern).matcher(string);
        m.find();
        return m.group(0);
    }

    public void logout() {
        if (isLoggedIn()) {
            isGuestMode = true;
            isLoggedIn = false;
            OKHTTPClient.get("http://58.177.253.171/it-school/php/buttons/itschool.php3", new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    if (preferences.contains("id")) {
                        preferences.edit().remove("id").remove("pwd").commit();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    if (preferences.contains("id")) {
                        preferences.edit().remove("id").remove("pwd").commit();
                    }
                }
            });
        }
    }

    public interface ChangePasswordCallback {
        int SUCCEED = 0;
        int FAILED_ILLEGAL_PWD = 1;
        int FAILED_SAME_PWD = 2;
        int FAILED_IO_EXCEPTION = 3;

        void onResult(int result);
    }

    public void changePassword(final String newPwd, final ChangePasswordCallback callback) {
        if (newPwd.matches("\\W") || newPwd.equals("")) {
            callback.onResult(ChangePasswordCallback.FAILED_ILLEGAL_PWD);
            return;
        }
        if (newPwd.equals(schoolAccount.getPassword())) {
            callback.onResult(ChangePasswordCallback.FAILED_SAME_PWD);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("oldpassword", schoolAccount.getPassword());
        map.put("newpassword", newPwd);
        map.put("confirmnewpassword", newPwd);
        OKHTTPClient.post("http://58.177.253.171/it-school/php/chpwd/index.php3", map, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                schoolAccount.setNewPassword(newPwd);
                callback.onResult(ChangePasswordCallback.SUCCEED);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onResult(ChangePasswordCallback.FAILED_IO_EXCEPTION);
            }
        });
    }

    public SchoolAccount getSchoolAccount() {
        return this.schoolAccount;
    }

    public boolean isGuestMode() {
        return isGuestMode;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isAutoLogin() {
        return isAutoLogin;
    }
}
