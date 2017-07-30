package com.xbyg_plus.silicon;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.utils.CachesDatabase;
import com.xbyg_plus.silicon.utils.DownloadsDatabase;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Logger.addLogAdapter(new AndroidLogAdapter());
        //initialize components with application context
        new DialogManager();
        new CachesDatabase(this);
        new DownloadsDatabase(this);
        new OKHTTPClient();
        new SchoolAccountHelper(this);
    }
}
