package com.xbyg_plus.silicon.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.callback.ChangePasswordCallback;
import com.xbyg_plus.silicon.callback.LoginCallback;
import com.xbyg_plus.silicon.callback.LogoutCallback;
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

public class SchoolAccountHelper {
    private static SchoolAccountHelper instance;

    private Context appContext;

    private boolean loggedIn = false;
    private SchoolAccount schoolAccount = null;

    public SchoolAccountHelper(Context appContext){
        instance = this;
        this.appContext = appContext;
    }

    public static SchoolAccountHelper getInstance(){
        return instance;
    }

    public SchoolAccount getSchoolAccount(){
        return this.schoolAccount;
    }

    /**
     * @see #loadMD5JS()
     * @see #encryptPwd(String, String, JsCallback)
     * This function loads MD5.js and encrypt the user password
     * Then post the login data to the server and initialize an school account.
     * @see SchoolAccount
     * */
    public void login(final String id,final String pwd, final LoginCallback callback){
        try {
            callback.onLoadEncryptionFile();
            String script = loadMD5JS();

            callback.onEncryptPassword();
            encryptPwd(script, pwd, new JsCallback() {
                @Override
                public void onResult(String encrypted_pwd) {
                    callback.onRequestLogin();
                    Map<String,String > postData = new HashMap<>();
                    postData.put("userloginid",id);
                    postData.put("password",encrypted_pwd);
                    //FakePassword and language fields can be ignored
                    OKHTTPClient.post("http://58.177.253.171/it-school/php/login_do.php3", postData,new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if(response.body().string().contains("main.php3")){
                                loggedIn = true;
                                initSchoolAccount(id,pwd,callback);
                            }else{
                                callback.onRequestLoginFailed(LoginCallback.LOGIN_FAILED_DATA_WRONG);
                            }
                        }
                        @Override
                        public void onFailure(Call call, IOException e) {
                            callback.onRequestLoginFailed(LoginCallback.LOGIN_FAILED_IO_EXCEPTION);
                        }
                    });
                }
                @Override
                public void onError(String s) {
                    callback.onEncryptPasswordFailed();
                }
            });
        }catch (IOException e){
            callback.onLoadEncryptionFileFailed();
        }
    }

    /**
     * This md5.js is a copy of the javascript of the following link
     * @link http://58.177.253.171/it-school//js/md5.js
     * */
    private String loadMD5JS() throws IOException{
        InputStream stream = appContext.getResources().openRawResource(R.raw.md5);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String context;
        while((context = reader.readLine()) != null){
            builder.append(context);
        }
        return builder.toString();
    }

    /**
     * The js-evaluator has to run on ui thread.
     *  It calls the MD5 function defined in the md5.js
     * @link https://github.com/evgenyneu/js-evaluator-for-android
     * */
    private void encryptPwd(String script, String pwd, JsCallback callback){
        new Handler(Looper.getMainLooper()).post(()->new JsEvaluator(appContext).callFunction(script,callback,"MD5",pwd));
    }

    /**
     * It send a request to http://58.177.253.171/it-school/php/home_v5.php3
     * then parsing the response to get student's name,class room and class number
     * */
    private void initSchoolAccount(final String id, final String pwd,final LoginCallback callback){
        if(schoolAccount == null && loggedIn){
            OKHTTPClient.call("http://58.177.253.171/it-school/php/home_v5.php3", new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Document doc = Jsoup.parse(response.body().string());
                    String welcome_msg = doc.select("div.right_content h1").first().text();
                    String name = getMatch(welcome_msg,"(?<=\\)\\ ).*(?=\\　)");
                    String classRoom = getMatch(welcome_msg,"(?<=\\()[1-6][A-F]");
                    int classNo = Integer.parseInt(getMatch(welcome_msg,"[0-9]{1,2}(?=\\))"));

                    schoolAccount = new SchoolAccount(name,classRoom,classNo,id,pwd);
                    callback.onRequestLoginSucceeded();
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onRequestLoginFailed(LoginCallback.LOGIN_FAILED_CANNOT_INIT_AC);
                }
            });
        }
    }

    private String getMatch(String string, String pattern){
        Matcher m = Pattern.compile(pattern).matcher(string);
        m.find();
        return m.group(0);
    }

    public void logout(final LogoutCallback callback){
        loggedIn = false;
        OKHTTPClient.call("http://58.177.253.171/it-school/php/buttons/itschool.php3", new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
                if(preferences.contains("id")){
                    preferences.edit().remove("id").remove("pwd").commit();
                }
                callback.onLoggedOut();
            }
            @Override
            public void onFailure(Call call, IOException e) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
                if(preferences.contains("id")){
                    preferences.edit().remove("id").remove("pwd").commit();
                }
                callback.onLoggedOut();
            }
        });
    }

    public void changePassword(final String newPwd, final ChangePasswordCallback callback){
        if(newPwd.matches("\\W")){
            callback.onFailed(ChangePasswordCallback.FAILED_ILLEGAL_PWD);
            return;
        }
        HashMap<String,String> map = new HashMap<>();
        map.put("oldpassword",schoolAccount.getPassword());
        map.put("newpassword",newPwd);
        map.put("confirmnewpassword",newPwd);
        OKHTTPClient.post("http://58.177.253.171/it-school/php/chpwd/index.php3", map, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                schoolAccount.setNewPassword(newPwd);
                callback.onPasswordChanged();
            }
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailed(ChangePasswordCallback.FAILED_IO_EXCEPTION);
            }
        });
    }
}
