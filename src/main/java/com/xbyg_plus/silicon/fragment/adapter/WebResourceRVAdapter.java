package com.xbyg_plus.silicon.fragment.adapter;

import android.app.Activity;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;

import com.xbyg_plus.silicon.activity.MainActivity;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ConfirmDialog;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.fragment.adapter.infoloader.WebResourcesInfoLoader;
import com.xbyg_plus.silicon.task.DownloadTask;
import com.xbyg_plus.silicon.utils.ItemSelector;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public abstract class WebResourceRVAdapter<ViewHolder extends RecyclerView.ViewHolder, Info extends WebResourceInfo, InfoLoader extends WebResourcesInfoLoader<Info>> extends RecyclerView.Adapter<ViewHolder> {
    protected Activity activity;

    protected ItemSelector<ItemSelector.SelectableItem, Info> selector;
    protected List<Info> resourcesList = new ArrayList<>();
    protected InfoLoader infoLoader;

    protected ConfirmDialog downloadConfirmDialog;

    protected WebResourceRVAdapter(Activity activity) {
        this.activity = activity;

        this.downloadConfirmDialog = new ConfirmDialog(activity);

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
        String nameList = "";
        for (WebResourceInfo info : selector.getSelectedItems().values()) {
            nameList += info.getName() + "\n";
        }

        downloadConfirmDialog.setContent(downloadConfirmDialog.getContext().getString(R.string.download_files_confirm), nameList)
                .setOnConfirmConsumer(confirm -> {
                    if (confirm) {
                        startDownload().subscribe();
                    }
                    selector.finish();
                }).show();
    }

    protected Observable<Info> startDownload() {
        String savingPath = PreferenceManager.getDefaultSharedPreferences(activity).getString("savingPath", Environment.getExternalStorageDirectory().getAbsolutePath()) + "/";

        return Observable.fromIterable(new ArrayList<>(selector.getSelectedItems().values()))
                .doOnNext(info -> new DownloadTask(savingPath).execute(info)) // If using method reference here (new DownloadTask(savingPath)::execute) , the DownloadTask objects in DownloadTask.pool will be duplicated
                .doOnComplete(() ->
                        Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.download_task_is_executing), Snackbar.LENGTH_LONG)
                                .setAction("SEE", v -> ((MainActivity) activity).showDownloadsFragment())
                                .show()
                );
    }

    @Override
    public int getItemCount() {
        return this.resourcesList.size();
    }
}
