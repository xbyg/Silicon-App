package com.xbyg_plus.silicon.fragment.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.dialog.ResDetailsDialog;
import com.xbyg_plus.silicon.model.WebPastPaperFolderInfo;
import com.xbyg_plus.silicon.model.WebPastPaperInfo;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.utils.CachesDatabase;
import com.xbyg_plus.silicon.fragment.adapter.infoloader.WebPastPaperInfoLoader;
import com.xbyg_plus.silicon.fragment.adapter.item.PastPaperItemView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastPaperRVAdapter extends WebResourceRVAdapter<WebResourceInfo, WebPastPaperInfoLoader> {
    //key: absolute path of folder   value: corresponding folder
    private Map<String, WebPastPaperFolderInfo> folderIndex = new HashMap<>();
    //key: absolute path of folder   value: corresponding contents(child folder and files)
    private Map<String, List<WebResourceInfo>> contentsIndex = new HashMap<>();
    private WebPastPaperFolderInfo currentFolder;

    private ResDetailsDialog resDetailsDialog;

    public PastPaperRVAdapter(Activity activity) {
        super(activity);
        this.infoLoader = new WebPastPaperInfoLoader();
        this.contentsIndex = CachesDatabase.contentsIndex;
        loadFolder(new WebPastPaperFolderInfo("root", "", "", null));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PastPaperItemView v = (PastPaperItemView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_past_paper, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WebResourceInfo resInfo = resourcesList.get(position);
        PastPaperItemView item = (PastPaperItemView) holder.item;

        item.getTitle().setText(resInfo.getName());
        if (resInfo instanceof WebPastPaperFolderInfo) {
            item.getIcon().setImageResource(R.drawable.folder);
            item.getCheckBox().setEnabled(false);
            item.getDescription().setText(resInfo.getDate());
            item.setOnClickListener(v -> loadFolder((WebPastPaperFolderInfo) resInfo));
        } else {
            item.getIcon().setImageResource(R.drawable.file);
            item.getCheckBox().setEnabled(true);
            item.getCheckBox().setChecked(selector.contains(resInfo));
            item.getCheckBox().setOnClickListener(v -> {
                if (((CheckBox) v).isChecked()) {
                    selector.add(resInfo);
                } else {
                    selector.remove(resInfo);
                }
            });
            item.getDescription().setText(resInfo.getSize() + "kb," + resInfo.getDate());
            item.setOnClickListener(v -> resDetailsDialog.setContent((WebPastPaperInfo) resInfo).show());
        }
    }

    public boolean backFolder() {
        if (currentFolder.getName().equals("root")) {
            return true;
        }
        this.currentFolder = folderIndex.get(currentFolder.getParentAbsolutePath());
        this.resourcesList = contentsIndex.get(currentFolder.getAbsolutePath());
        updateView();
        return false;
    }

    public void loadFolder(WebPastPaperFolderInfo folder) {
        if (contentsIndex.containsKey(folder.getAbsolutePath())) {
            folderIndex.put(folder.getAbsolutePath(), folder);
            currentFolder = folder;
            resourcesList = contentsIndex.get(folder.getAbsolutePath());
            updateView();
        } else {
            final WebPastPaperInfoLoader.RequestParams reqParams = new WebPastPaperInfoLoader.RequestParams();
            reqParams.folderInfo = folder;
            this.infoLoader.request(reqParams, parsedList -> {
                folderIndex.put(folder.getAbsolutePath(), folder);
                currentFolder = reqParams.folderInfo;
                resourcesList = parsedList;
                contentsIndex.put(reqParams.folderInfo.getAbsolutePath(), parsedList);
                updateView();
            });
        }
    }

    @Override
    public void refreshData() {
        this.folderIndex.clear();
        this.contentsIndex.clear();
        this.resourcesList.clear();
        this.loadFolder(new WebPastPaperFolderInfo("root", "", "", null));
    }

    @Override
    public void requestDialogs(DialogManager dialogManager) {
        super.requestDialogs(dialogManager);
        this.resDetailsDialog = dialogManager.obtain(ResDetailsDialog.class);
    }

    @Override
    public void releaseDialogs() {
        super.releaseDialogs();
        this.resDetailsDialog = null;
    }
}
