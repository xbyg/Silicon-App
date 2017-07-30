package com.xbyg_plus.silicon.fragment.adapter.infoloader;

import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.dialog.LoadingDialog;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public abstract class WebResourcesInfoLoader<T extends WebResourceInfo> implements DialogManager.DialogHolder {
    protected LoadingDialog loadingDialog;

    public WebResourcesInfoLoader() {
        DialogManager.registerDialogHolder(this);
    }

    public static class RequestParameters {
    }

    public interface LoadCallback<T extends WebResourceInfo> {
        void onLoaded(List<T> parsedList);
    }

    public abstract void request(RequestParameters parameters, LoadCallback callback);

    protected abstract List<T> parseResponse(RequestParameters parameters, Response response) throws IOException;

    @Override
    public void requestDialogs(DialogManager dialogManager) {
        this.loadingDialog = dialogManager.obtain(LoadingDialog.class);
    }

    @Override
    public void releaseDialogs() {
        this.loadingDialog = null;
    }
}
