package com.xbyg_plus.silicon.adapter;

import android.app.Activity;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;

import com.xbyg_plus.silicon.MainActivity;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ConfirmDialog;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.infoloader.WebResourcesInfoLoader;
import com.xbyg_plus.silicon.task.DownloadTask;
import com.xbyg_plus.silicon.utils.ItemSelector;
import com.xbyg_plus.silicon.view.ResItemView;

import java.util.ArrayList;
import java.util.List;

public abstract class WebResourceRVAdapter<Info extends WebResourceInfo, InfoLoader extends WebResourcesInfoLoader<Info>> extends RecyclerView.Adapter<WebResourceRVAdapter.ViewHolder> {
    protected Activity activity;

    protected ItemSelector<Info> selector;
    protected List<Info> resourcesList = new ArrayList<>();
    protected InfoLoader infoLoader;

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ResItemView item;

        public ViewHolder(ResItemView item) {
            super(item);
            this.item = item;
        }
    }

    protected WebResourceRVAdapter(Activity activity) {
        this.activity = activity;
        this.selector = new ItemSelector<>(activity, R.menu.file_download);
        this.selector.setActionModeListener(new ItemSelector.ActionModeListener() {
            @Override
            public void onActionItemClicked(int itemID) {
                if (itemID == R.id.action_download) {
                    showDownloadConfirm();
                }
            }

            @Override
            public void onDestroyActionMode() {
                selector.getSelectedItems().clear();
                notifyDataSetChanged();
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
        activity.runOnUiThread(() -> {
            notifyDataSetChanged();
        });
    }

    public abstract void refreshData();

    protected void showDownloadConfirm() {
        String nameList = "";
        for (Info info : selector.getSelectedItems()) {
            nameList += info.getName() + "\n";
        }
        new ConfirmDialog(activity, activity.getString(R.string.download_files_confirm), nameList, confirmation -> {
            if (confirmation) {
                String savePath = PreferenceManager.getDefaultSharedPreferences(activity).getString("savingPath", "/sdcard") + "/";
                List<Info> resInfoList = new ArrayList(selector.getSelectedItems());
                for (Info resInfo : resInfoList) {
                    new DownloadTask<Info>(activity, savePath).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, resInfo);
                }
                selector.getSelectedItems().clear();
                Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.download_task_is_executing), Snackbar.LENGTH_LONG)
                        .setAction("SEE", v -> {
                            ((MainActivity) activity).showDownloadsFragment();
                        })
                        .show();
            }
            selector.finish();
        });
    }

    @Override
    public int getItemCount() {
        return this.resourcesList.size();
    }
}
