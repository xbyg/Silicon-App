package com.xbyg_plus.silicon.event;

import com.xbyg_plus.silicon.task.DownloadTask;

import java.io.File;

public class DownloadCompleteEvent {
    private DownloadTask downloadTask;
    private File file;

    public DownloadCompleteEvent(DownloadTask downloadTask,File file){
        this.downloadTask = downloadTask;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public DownloadTask getDownloadTask() {
        return downloadTask;
    }
}
