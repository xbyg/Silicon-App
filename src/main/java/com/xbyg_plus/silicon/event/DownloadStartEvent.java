package com.xbyg_plus.silicon.event;

import com.xbyg_plus.silicon.task.DownloadTask;

public class DownloadStartEvent {
    private DownloadTask downloadTask;

    public DownloadStartEvent(DownloadTask downloadTask){
        this.downloadTask = downloadTask;
    }

    public DownloadTask getDownloadTask() {
        return downloadTask;
    }
}
