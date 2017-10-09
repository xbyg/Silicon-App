package com.xbyg_plus.silicon.model;

public class Notification {
    private String title;

    private String message;

    private long date;

    public Notification(String title, String message, long date) {
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getDate() {
        return date;
    }
}
