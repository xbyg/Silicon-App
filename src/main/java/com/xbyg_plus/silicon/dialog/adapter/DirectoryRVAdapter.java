package com.xbyg_plus.silicon.dialog.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.fragment.adapter.item.PastPaperItemView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DirectoryRVAdapter extends RecyclerView.Adapter<ViewHolder> {
    private File currentDirectory;

    // All valid child directories under current directory
    private List<File> dirs = new ArrayList<>();
    private File selectedDir;

    private SimpleDateFormat simpleDateFormat;

    public DirectoryRVAdapter() {
        this.simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PastPaperItemView root = (PastPaperItemView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_past_paper, parent, false);
        root.getIcon().setImageResource(R.drawable.folder);
        return new RecyclerView.ViewHolder(root) {};
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File dir = dirs.get(position);
        PastPaperItemView itemView = (PastPaperItemView) holder.itemView;

        itemView.setOnClickListener(v -> loadDir(dir));

        itemView.getCheckBox().setEnabled(selectedDir == null || selectedDir.equals(dir));
        itemView.getCheckBox().setChecked(dir.equals(selectedDir)); //Without this,the checkbox disappears,bug?
        itemView.getCheckBox().setOnClickListener(v -> {
            selectedDir = ((CheckBox) v).isChecked() ? dir : null;
            notifyDataSetChanged();//enable or disable other checkboxes
        });

        itemView.getTitle().setText(dir.getName());
        itemView.getDescription().setText(this.simpleDateFormat.format(new Date(dir.lastModified())));
    }

    public boolean loadDir(File directory) {
        if (!directory.equals(new File("/"))) {
            File[] loadedDirs = directory.listFiles(file -> file.isDirectory() && file.canWrite() && !file.isHidden());

            if (loadedDirs != null && loadedDirs.length != 0) {
                currentDirectory = directory;
                selectedDir = null;
                dirs = Arrays.asList(loadedDirs);

                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public File getSelectedDir() {
        return selectedDir;
    }

    @Override
    public int getItemCount() {
        return dirs.size();
    }
}
