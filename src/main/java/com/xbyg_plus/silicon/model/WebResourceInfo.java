package com.xbyg_plus.silicon.model;

import android.support.annotation.Nullable;

import java.io.Serializable;

public abstract class WebResourceInfo implements Serializable {
    protected String name;
    @Nullable
    protected float size; //in kb
    @Nullable
    protected String date;
    @Nullable
    protected String downloadAddress;

    public String getName() {
        return name;
    }

    @Nullable
    public float getSize() {
        return size;
    }

    @Nullable
    public String getDate() {
        return date;
    }

    @Nullable
    public String getDownloadAddress() {
        return downloadAddress;
    }
}
