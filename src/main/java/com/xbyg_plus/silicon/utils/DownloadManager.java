package com.xbyg_plus.silicon.utils;

import android.preference.PreferenceManager;

import com.xbyg_plus.silicon.MyApplication;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.task.DownloadTask;

import java.io.File;
import java.util.LinkedList;

import io.reactivex.android.schedulers.AndroidSchedulers;

public final class DownloadManager {
    private static DownloadTaskListener listener;

    private static LinkedList<DownloadTask> startedTasks = new LinkedList<>();
    private static String savePath = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("savingPath", "/sdcard") + "/";

    public static void download(WebResourceInfo resInfo) {
        DownloadTask task = new DownloadTask(savePath);
        
        task.execute(resInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> {
                    startedTasks.remove(task);
                    if (listener != null) {
                        listener.onDownloadFinish(task, file);
                    }
                }, throwable -> {
                    startedTasks.remove(task);
                    if (listener != null) {
                        listener.onDownloadError(throwable);
                    }
                });

        startedTasks.add(task);
        if (listener != null) {
            listener.onDownloadStart(task);
        }
    }

    public static LinkedList<DownloadTask> getStartedTasks() {
        return startedTasks;
    }

    public static void setDownloadTaskListener(DownloadTaskListener listener) {
        if (listener != null) {
            DownloadManager.listener = listener;
        }
    }

    public interface DownloadTaskListener {
        void onDownloadStart(DownloadTask task);
        
        void onDownloadFinish(DownloadTask task, File file);
        
        void onDownloadError(Throwable throwable);
    }
}
