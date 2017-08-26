package com.xbyg_plus.silicon.fragment.adapter;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebPastPaperFolderInfo;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.database.CachesDatabase;
import com.xbyg_plus.silicon.fragment.adapter.infoloader.WebPastPaperInfoLoader;
import com.xbyg_plus.silicon.fragment.adapter.item.PastPaperItemView;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.utils.ViewIntent;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.schedulers.Schedulers;

public class PastPaperRVAdapter extends WebResourceRVAdapter<WebResourceInfo, WebPastPaperInfoLoader> {
    //key: absolute path of folder   value: corresponding folder
    private Map<String, WebPastPaperFolderInfo> folderIndex = new HashMap<>();
    //key: absolute path of folder   value: corresponding contents(child folder and files)
    private Map<String, List<WebResourceInfo>> contentsIndex = new HashMap<>();
    private WebPastPaperFolderInfo currentFolder;

    public PastPaperRVAdapter(Activity activity) {
        super(activity);
        this.infoLoader = new WebPastPaperInfoLoader(activity);
        this.contentsIndex = CachesDatabase.getContentsIndex();
        loadFolder(WebPastPaperFolderInfo.rootFolder);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PastPaperItemView v = (PastPaperItemView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_past_paper, parent, false);
        return new ViewHolder(v) {};
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WebResourceInfo resInfo = resourcesList.get(position);
        PastPaperItemView item = (PastPaperItemView) holder.itemView;

        item.getTitle().setText(resInfo.getName());
        item.getCheckBox().setChecked(selector.containsValue(resInfo));
        if (resInfo instanceof WebPastPaperFolderInfo) {
            item.getIcon().setImageResource(R.drawable.folder);
            item.getCheckBox().setEnabled(false);
            item.getDescription().setText(resInfo.getDate());
            item.setOnClickListener(v -> loadFolder((WebPastPaperFolderInfo) resInfo));
        } else {
            item.getIcon().setImageResource(R.drawable.file);
            item.getCheckBox().setEnabled(true);
            item.getCheckBox().setOnClickListener(v -> {
                if (((CheckBox) v).isChecked()) {
                    selector.select(item, resInfo);
                } else {
                    selector.deselect(item);
                }
            });
            item.getDescription().setText(resInfo.getSize() + "kb," + resInfo.getDate());

            item.setOnClickListener(v -> {
                OKHTTPClient.stream(resInfo.getDownloadAddress())
                        .observeOn(Schedulers.io())
                        .subscribe(inStream -> {
                            File tempFile = File.createTempFile("temp", "."+FilenameUtils.getExtension(resInfo.getName()), Environment.getExternalStorageDirectory());
                            tempFile.deleteOnExit();
                            IOUtils.copy(inStream, new FileOutputStream(tempFile));
                            ViewIntent.view(activity, Uri.fromFile(tempFile));
                        });
            });
        }
    }

    public boolean backFolder() {
        if (currentFolder.getName().equals("root")) {
            return true;
        }
        loadFolder(folderIndex.get(currentFolder.getParentAbsolutePath()));
        return false;
    }

    public void loadFolder(WebPastPaperFolderInfo folder) {
        if (contentsIndex.containsKey(folder.getAbsolutePath())) {
            applyFolderContents(folder, contentsIndex.get(folder.getAbsolutePath()));
        } else {
            WebPastPaperInfoLoader.RequestParams reqParams = new WebPastPaperInfoLoader.RequestParams();
            reqParams.folderInfo = folder;
            this.infoLoader.request(reqParams)
                    .subscribe(parsedList -> {
                        contentsIndex.put(folder.getAbsolutePath(), parsedList);
                        applyFolderContents(folder, parsedList);
                    });
        }
    }

    private void applyFolderContents(WebPastPaperFolderInfo folder, List<WebResourceInfo> contents) {
        folderIndex.put(folder.getAbsolutePath(), folder);
        currentFolder = folder;
        resourcesList = contents;
        updateView();
    }

    @Override
    public void refreshData() {
        this.folderIndex.clear();
        this.contentsIndex.clear();
        this.resourcesList.clear();
        this.loadFolder(WebPastPaperFolderInfo.rootFolder);
    }
}
