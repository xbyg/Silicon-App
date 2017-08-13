package com.xbyg_plus.silicon.fragment.adapter;

import android.app.Activity;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;

import com.xbyg_plus.silicon.activity.MainActivity;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ConfirmDialog;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.fragment.adapter.item.SelectableItemView;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.fragment.adapter.infoloader.WebResourcesInfoLoader;
import com.xbyg_plus.silicon.utils.DownloadManager;
import com.xbyg_plus.silicon.utils.ItemSelector;

import java.util.ArrayList;
import java.util.List;

public abstract class WebResourceRVAdapter<Info extends WebResourceInfo, InfoLoader extends WebResourcesInfoLoader<Info>> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DialogManager.DialogHolder {
    protected Activity activity;

    protected ItemSelector<SelectableItemView, Info> selector;
    protected List<Info> resourcesList = new ArrayList<>();
    protected InfoLoader infoLoader;

    protected ConfirmDialog downloadConfirmDialog;

    protected WebResourceRVAdapter(Activity activity) {
        DialogManager.registerDialogHolder(this);
        this.activity = activity;
        this.selector = new ItemSelector<>(activity, R.menu.web_res_operation);
        this.selector.setActionItemClickListener(itemID -> {
            if (itemID == R.id.action_download) {
                showDownloadConfirm();
            }
        });
    }

    public List<Info> getResourcesList() {
        return this.resourcesList;
    }

    public InfoLoader getInfoLoader() {
        return this.infoLoader;
    }

    public void updateView() {
        activity.runOnUiThread(() -> notifyDataSetChanged());
    }

    public abstract void refreshData();

    protected void showDownloadConfirm() {
        downloadConfirmDialog.confirmObservable()
                .subscribe(confirm -> {
                    if (confirm) {
                        List<Info> resInfoList = new ArrayList<>(selector.getSelectedItems().values());
                        for (Info resInfo : resInfoList) {
                            DownloadManager.download(resInfo);
                        }
                        Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.download_task_is_executing), Snackbar.LENGTH_LONG)
                                .setAction("SEE", v -> ((MainActivity) activity).showDownloadsFragment())
                                .show();
                    }
                    selector.finish();
                });

        String nameList = "";
        for (WebResourceInfo info : selector.getSelectedItems().values()) {
            nameList += info.getName() + "\n";
        }
        downloadConfirmDialog.setContent(downloadConfirmDialog.getContext().getString(R.string.download_files_confirm), nameList)
                .show();
    }

    @Override
    public void onDialogsCreated(DialogManager dialogManager) {
        this.downloadConfirmDialog = dialogManager.obtain(ConfirmDialog.class);
    }

    @Override
    public int getItemCount() {
        return this.resourcesList.size();
    }
}
