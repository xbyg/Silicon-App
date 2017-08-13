package com.xbyg_plus.silicon;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.database.CachesDatabase;
import com.xbyg_plus.silicon.database.DownloadsDatabase;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Logger.addLogAdapter(new AndroidLogAdapter());
        //initialize components with application context
        DialogManager.init();
        CachesDatabase.init(this);
        DownloadsDatabase.init(this);
        OKHTTPClient.init();
        SchoolAccountHelper.init(this);
    }

    public static Application getContext() {
        return instance;
    }
}
