package com.xbyg_plus.silicon.model;

public class WebNoticeInfo extends WebResourceInfo {
    private String id;

    public WebNoticeInfo(String name, String id, String downloadAddress) {
        this.name = name;
        this.id = id;
        this.downloadAddress = downloadAddress;
    }

    public String getId() {
        return id;
    }

    public void setDownloadAddress(String downloadAddress) {
        this.downloadAddress = downloadAddress;
    }
}
