package com.xbyg_plus.silicon.fragment.adapter.infoloader;

import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.dialog.LoadingDialog;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.io.IOException;
import java.util.List;

import io.reactivex.Single;

public abstract class WebResourcesInfoLoader<T extends WebResourceInfo> implements DialogManager.DialogHolder {
    protected LoadingDialog loadingDialog;

    public WebResourcesInfoLoader() {
        DialogManager.registerDialogHolder(this);
    }

    public static class RequestParameters {
    }

    public abstract Single<List<T>> request(RequestParameters parameters);

    protected abstract List<T> parseResponse(RequestParameters parameters, String htmlString);

    @Override
    public void onDialogsCreated(DialogManager dialogManager) {
        this.loadingDialog = dialogManager.obtain(LoadingDialog.class);
    }
}
