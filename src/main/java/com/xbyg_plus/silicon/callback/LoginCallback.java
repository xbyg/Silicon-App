package com.xbyg_plus.silicon.callback;

public class LoginCallback {
    public final static int LOGIN_FAILED_DATA_WRONG = 0;
    public final static int LOGIN_FAILED_IO_EXCEPTION = 1;
    public final static int LOGIN_FAILED_CANNOT_INIT_AC = 2;

    public void onLoadEncryptionFile() {
    }

    public void onLoadEncryptionFileFailed() {
    }

    public void onEncryptPassword() {
    }

    public void onEncryptPasswordFailed() {
    }

    public void onRequestLogin() {
    }

    public void onRequestLoginSucceeded() {
    }

    public void onRequestLoginFailed(int reason) {
    }
}
