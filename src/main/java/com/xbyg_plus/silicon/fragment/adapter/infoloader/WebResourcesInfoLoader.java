package com.xbyg_plus.silicon.fragment.adapter.infoloader;

import android.content.Context;

import com.xbyg_plus.silicon.dialog.LoadingDialog;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.util.List;

import io.reactivex.Single;

public abstract class WebResourcesInfoLoader<T extends WebResourceInfo> {
    protected LoadingDialog loadingDialog;

    public WebResourcesInfoLoader(Context context) {
        this.loadingDialog = new LoadingDialog(context);
    }

    public static class RequestParameters {
    }

    public abstract Single<List<T>> request(RequestParameters parameters);

    protected abstract List<T> parseResponse(RequestParameters parameters, String htmlString);
}
