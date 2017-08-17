package com.xbyg_plus.silicon.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xbyg_plus.silicon.MyApplication;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.SchoolAccount;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.schedulers.Schedulers;

public final class SchoolAccountHelper {
    private static SchoolAccountHelper instance;

    private MyApplication context;
    private SharedPreferences preferences;

    private SchoolAccount schoolAccount;
    private boolean isGuestMode = true;
    private boolean isAutoLogin = false;
    private boolean isLoggedIn = false;

    private SchoolAccountHelper(MyApplication context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.isAutoLogin = preferences.contains("id");
    }

    public static void init(MyApplication context) {
        if (instance == null) {
            instance = new SchoolAccountHelper(context);
        }
    }

    public static SchoolAccountHelper getInstance() {
        return instance;
    }

    public Completable tryAutoLogin() {
        if (isAutoLogin) {
            return login(preferences.getString("id", ""), preferences.getString("pwd", ""));
        }
        return Completable.error(new RuntimeException("Auto login failed"));
    }

    public Completable login(String id, String pwd) {
        return encryptPwd(pwd)
                .observeOn(Schedulers.io())
                .flatMap(encryptedPwd -> {
                    OKHTTPClient.getCookieStore().clear();
                    //first time login: we save "PHPSESSID" and "sessionid" cookies from the response
                    //second time login (without restarting the app): we send the old "PHPSESSID" and "sessionid" for request and then server responses a new "sessionid" but does not contain a "PHPSESSID"
                    //thus, for further request, server responses a html that only contains error message (http://58.177.253.171/it-school//php/errormessage.php3?error=1)
                    //to solve this problem, we clear the cookies before request for login

                    Map<String, String> postData = new HashMap<>();
                    postData.put("userloginid", id);
                    postData.put("password", encryptedPwd);
                    return OKHTTPClient.post("http://58.177.253.171/it-school/php/login_do.php3", postData);
                })
                .flatMapCompletable(htmlString -> htmlString.contains("main.php3") ? initSchoolAccount(id, pwd) : Completable.error(new RuntimeException(context.getString(R.string.login_data_wrong))));
    }

    private Single<String> encryptPwd(String pwd) {
        return Single.create((SingleEmitter<String> e) -> {
            byte[] encryptedPwdBytes = MessageDigest.getInstance("MD5").digest(pwd.getBytes("UTF-8"));

            // convert encrypted password's byte array to hex string
            // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
            char[] hexArray = "0123456789ABCDEF".toCharArray();
            char[] hexChars = new char[encryptedPwdBytes.length * 2];
            for (int j = 0; j < encryptedPwdBytes.length; j++) {
                int v = encryptedPwdBytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            String encryptedPwd = new String(hexChars).toLowerCase();
            e.onSuccess(encryptedPwd);
        }).subscribeOn(Schedulers.computation());
    }

    private Completable initSchoolAccount(String id, String pwd) {
        return Completable.create(e ->
                OKHTTPClient.get("http://58.177.253.171/it-school/php/home_v5.php3")
                        .observeOn(Schedulers.computation())
                        .subscribe(htmlString -> {
                            Document doc = Jsoup.parse(htmlString);
                            String welcome_msg = doc.select("div.right_content h1").first().text();
                            String name = getMatch(welcome_msg, "(?<=\\)\\ ).*(?=\\　)");
                            String classRoom = getMatch(welcome_msg, "(?<=\\()[1-6][A-F]");
                            int classNo = Integer.parseInt(getMatch(welcome_msg, "[0-9]{1,2}(?=\\))"));

                            schoolAccount = new SchoolAccount(name, classRoom, classNo, id, pwd);
                            isGuestMode = false;
                            isLoggedIn = true;
                            isAutoLogin = true;
                            preferences.edit().putString("id", id).putString("pwd", pwd).apply();

                            e.onComplete();
                        }, throwable -> e.onError(new IOException(context.getString(R.string.login_io_exception))))
        );
    }

    private String getMatch(String string, String pattern) {
        Matcher m = Pattern.compile(pattern).matcher(string);
        m.find();
        return m.group(0);
    }

    public void logout() {
        if (isLoggedIn) {
            schoolAccount = null;
            isGuestMode = true;
            isLoggedIn = false;
            isAutoLogin = false;
            preferences.edit().remove("id").remove("pwd").apply();
            OKHTTPClient.get("http://58.177.253.171/it-school/php/buttons/itschool.php3")
                    .subscribe(htmlString -> {}, throwable -> {});
        }
    }

    public Completable changePassword(String newPwd) {
        if (newPwd.matches("\\W") || newPwd.equals("")) {
            return Completable.error(new RuntimeException(context.getString(R.string.change_pwd_illegal_pwd)));
        }
        if (newPwd.equals(schoolAccount.getPassword())) {
            return Completable.error(new RuntimeException(context.getString(R.string.change_pwd_same_pwd)));
        }

        HashMap<String, String> postData = new HashMap<>();
        postData.put("oldpassword", schoolAccount.getPassword());
        postData.put("newpassword", newPwd);
        postData.put("confirmnewpassword", newPwd);
        return OKHTTPClient.post("http://58.177.253.171/it-school/php/chpwd/index.php3", postData)
                .toCompletable()
                .doOnComplete(() -> schoolAccount.setNewPassword(newPwd));
    }

    /**
     * We have to login to MTV if we want to like a video but there are some problems:
     * 1. The login password for MTV is student's HKID card number which is not the same as the password for school internet,
     * it causes a lot of anonymous classes and callbacks (sign in button's click listener -> LoginCallback -> MTVLoginCallback......),
     * so we should make a clearer way to overcome this problem.
     *
     * 2. Server side requests us to enable javascript which we can't do it easily....
     * */
    public Completable loginMTV(String HKid) {
        //loadingDialog.setMessage("", context.getString(R.string.requesting, "http://58.177.253.163/mtv/signup.php"));
        //loadingDialog.show();

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", schoolAccount.getId());
        postData.put("password", HKid);
        postData.put("login", "Login");
        return OKHTTPClient.post("http://58.177.253.163/mtv/signup.php", postData)
                //.doOnError(throwable -> loadingDialog.dismiss(context.getString(R.string.io_exception)))
                .flatMapCompletable(htmlString -> {
                    //<script type="text/javascript">window.location = "http://58.177.253.163/mtv/videos.php?cat=all&sort=view_all&time=all_time&page=1"</script>Javascript is turned off, <a href='http://58.177.253.163/mtv/videos.php?cat=all&sort=view_all&time=all_time&page=1'>click here to go to requested page</a>
                    if (htmlString.contains("window.location = \"http://58.177.253.163/mtv/videos.php\"")) {
                        schoolAccount.setHKid(HKid);
                       // loadingDialog.dismiss();
                        return Completable.complete();
                    } else {
                        //loadingDialog.dismiss(context.getString(R.string.login_mtv_data_wrong));
                        return Completable.error(new Exception("user data wrong"));
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
