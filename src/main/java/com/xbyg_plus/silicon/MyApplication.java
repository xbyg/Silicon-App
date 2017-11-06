package com.xbyg_plus.silicon;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.xbyg_plus.silicon.utils.OKHTTPClient;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Logger.addLogAdapter(new AndroidLogAdapter());
        OKHTTPClient.init();
        Fabric.with(this, new Crashlytics());
    }

    public static Application getContext() {
        return instance;
    }
}
