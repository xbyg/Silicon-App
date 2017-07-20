package com.xbyg_plus.silicon.model;

public class WebNoticeInfo extends WebResourceInfo {
    private String id;
    private String startDate;
    private String effectiveDate;
    private String uploader;

    public WebNoticeInfo(String name, String id, String startDate, String effectiveDate, String uploader) {
        this.name = name;
        this.id = id;
        this.startDate = startDate;
        this.effectiveDate = effectiveDate;
        this.uploader = uploader;
    }

    public String getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getUploader() {
        return uploader;
    }

    public void setDownloadAddress(String address) {
        this.downloadAddress = address;
    }
}
