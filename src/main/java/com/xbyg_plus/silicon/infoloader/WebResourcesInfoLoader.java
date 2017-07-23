package com.xbyg_plus.silicon.infoloader;

import android.app.Activity;

import com.xbyg_plus.silicon.dialog.LoadingDialog;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public abstract class WebResourcesInfoLoader<T extends WebResourceInfo> {
    protected Activity activity;
    protected LoadingDialog loadingDialog;

    public WebResourcesInfoLoader(Activity activity) {
        this.activity = activity;
        this.loadingDialog = new LoadingDialog(activity);
    }

    public static class RequestParameters {
    }

    public static class LoadCallback<T extends WebResourceInfo> {
        public void onLoaded(RequestParameters params, List<T> parsedList) {
        }
    }

    public abstract void request(RequestParameters parameters, LoadCallback callback);

    protected abstract List<T> parseResponse(RequestParameters parameters, Response response) throws IOException;
}
