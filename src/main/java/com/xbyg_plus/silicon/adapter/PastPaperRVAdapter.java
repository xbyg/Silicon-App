package com.xbyg_plus.silicon.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ResDetailsDialog;
import com.xbyg_plus.silicon.model.WebPastPaperFolderInfo;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.utils.CachesDatabase;
import com.xbyg_plus.silicon.infoloader.WebPastPaperInfoLoader;
import com.xbyg_plus.silicon.infoloader.WebResourcesInfoLoader;
import com.xbyg_plus.silicon.view.PastPaperItemView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastPaperRVAdapter extends WebResourceRVAdapter<WebResourceInfo,WebPastPaperInfoLoader>{
    private Map<String,List<WebResourceInfo>> foldersInfo = new HashMap<>();
    private WebPastPaperFolderInfo currentFolder;

    public PastPaperRVAdapter(Activity activity){
        super(activity);
        this.infoLoader = new WebPastPaperInfoLoader(activity);
        this.foldersInfo = CachesDatabase.pastPaperFolders;
        loadFolder(new WebPastPaperFolderInfo("root","",null,null));
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
        if(resInfo instanceof WebPastPaperFolderInfo){
            item.getIcon().setImageResource(R.drawable.folder);
            item.getCheckBox().setEnabled(false);
            item.getDescription().setText(resInfo.getDate());
            item.setOnClickListener(v->loadFolder((WebPastPaperFolderInfo) resInfo));
        }else{
            item.getIcon().setImageResource(R.drawable.file);
            item.getCheckBox().setEnabled(true);
            item.getCheckBox().setChecked(selector.contains(resInfo));
            item.getCheckBox().setOnClickListener(v->{
                if(((CheckBox)v).isChecked()){
                    selector.add(resInfo);
                }else{
                    selector.remove(resInfo);
                }
            });
            item.getDescription().setText(resInfo.getSize()+"kb,"+ resInfo.getDate());
            item.setOnClickListener(v->new ResDetailsDialog(activity,resInfo).show());
        }
    }

    public boolean backFolder(){
        if(currentFolder.getName().equals("root")){
            return true;
        }
        this.currentFolder = currentFolder.getParentFolder();
        this.resourcesList = foldersInfo.get(currentFolder.getName());
        updateView();
        return false;
    }

    public void loadFolder(WebPastPaperFolderInfo folder) {
        if (foldersInfo.containsKey(folder.getName())) {
            this.currentFolder = folder;
            this.resourcesList = foldersInfo.get(folder.getName());
            updateView();
        } else {
            final WebPastPaperInfoLoader.RequestParams params = new WebPastPaperInfoLoader.RequestParams();
            params.folderInfo = folder;
            this.infoLoader.request(params, new WebResourcesInfoLoader.LoadCallback() {
                @Override
                public void onLoaded(WebResourcesInfoLoader.RequestParameters parameters, List parsedList) {
                    currentFolder = params.folderInfo;
                    resourcesList = parsedList;
                    foldersInfo.put(params.folderInfo.getName(),parsedList);
                    updateView();
                }
            });
        }
    }

    @Override
    public void refreshData() {
        this.foldersInfo.clear();
        this.resourcesList.clear();
        this.loadFolder(new WebPastPaperFolderInfo("root","",null,null));
    }
}
