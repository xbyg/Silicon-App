package com.xbyg_plus.silicon.callback;

public class ChangePasswordCallback {
    public static final int FAILED_ILLEGAL_PWD = 0;
    public static final int FAILED_SAME_PWD = 1;
    public static final int FAILED_IO_EXCEPTION = 2;

    public void onPasswordChanged(){}
    public void onFailed(int reason){}
}
