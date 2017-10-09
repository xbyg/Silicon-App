package com.xbyg_plus.silicon.fragment.adapter;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebPastPaperFolderInfo;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.fragment.adapter.infoloader.WebPastPaperInfoLoader;
import com.xbyg_plus.silicon.utils.ItemSelector;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.utils.ViewIntent;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class PastPaperRVAdapter extends WebResourceRVAdapter<PastPaperRVAdapter.ViewHolder, WebResourceInfo, WebPastPaperInfoLoader> {
    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemSelector.SelectableItem {
        CheckBox checkBox;
        ImageView icon;
        TextView title;
        TextView description;
        ImageView view;

        ViewHolder(View root) {
            super(root);
            checkBox = root.findViewById(R.id.checkbox);
            icon = root.findViewById(R.id.icon);
            title = root.findViewById(R.id.title);
            description = root.findViewById(R.id.description);
            view = root.findViewById(R.id.view);
        }

        @Override
        public void onSelected() {
            checkBox.setChecked(true);
        }

        @Override
        public void onDeselected() {
            checkBox.setChecked(false);
        }
    }

    //key: absolute path of folder   value: corresponding folder
    private Map<String, WebPastPaperFolderInfo> folderIndex = new HashMap<>();
    //key: absolute path of folder   value: corresponding contents(child folder and files)
    private Map<String, List<WebResourceInfo>> contentsIndex = new HashMap<>();
    private WebPastPaperFolderInfo currentFolder;

    public PastPaperRVAdapter(Activity activity, Single<Map<String, List<WebResourceInfo>>> dataSource) {
        super(activity);
        this.infoLoader = new WebPastPaperInfoLoader(activity);
        dataSource.subscribe(contentsIndex -> {
            this.contentsIndex = contentsIndex;
            loadFolder(WebPastPaperFolderInfo.rootFolder);
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WebResourceInfo resInfo = resourcesList.get(position);

        holder.title.setText(resInfo.getName());
        holder.checkBox.setChecked(selector.containsValue(resInfo));
        if (resInfo instanceof WebPastPaperFolderInfo) {
            holder.icon.setImageResource(R.drawable.folder);
            holder.checkBox.setEnabled(false);
            holder.description.setText(resInfo.getDate());
            holder.itemView.setOnClickListener(v -> loadFolder((WebPastPaperFolderInfo) resInfo));
        } else {
            holder.icon.setImageResource(R.drawable.file);
            holder.checkBox.setEnabled(true);
            holder.checkBox.setOnClickListener(v -> {
                if (((CheckBox) v).isChecked()) {
                    selector.select(holder, resInfo);
                } else {
                    selector.deselect(holder);
                }
            });
            holder.description.setText(resInfo.getSize() + "kb," + resInfo.getDate());

            holder.itemView.setOnClickListener(v -> {
                OKHTTPClient.stream(resInfo.getDownloadAddress())
                        .observeOn(Schedulers.io())
                        .subscribe(inStream -> {
                            File tempFile = File.createTempFile("temp", "." + FilenameUtils.getExtension(resInfo.getName()), Environment.getExternalStorageDirectory());
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
