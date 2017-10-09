package com.xbyg_plus.silicon.dialog.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.utils.ItemSelector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DirectoryRVAdapter extends RecyclerView.Adapter<DirectoryRVAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemSelector.SelectableItem {
        public CheckBox checkBox;
        public TextView title;
        public TextView description;
        public ImageView view;

        public ViewHolder(View root) {
            super(root);
            checkBox = root.findViewById(R.id.checkbox);
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File dir = dirs.get(position);

        holder.itemView.setOnClickListener(v -> loadDir(dir));

        holder.checkBox.setEnabled(selectedDir == null || selectedDir.equals(dir));
        holder.checkBox.setChecked(dir.equals(selectedDir)); //Without this,the checkbox disappears,bug?
        holder.checkBox.setOnClickListener(v -> {
            selectedDir = ((CheckBox) v).isChecked() ? dir : null;
            notifyDataSetChanged();//enable or disable other checkboxes
        });

        holder.title.setText(dir.getName());
        holder.description.setText(this.simpleDateFormat.format(new Date(dir.lastModified())));
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
