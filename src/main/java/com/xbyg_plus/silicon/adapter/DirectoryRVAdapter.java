package com.xbyg_plus.silicon.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.callback.DirectorySelectedCallback;
import com.xbyg_plus.silicon.view.PastPaperItemView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DirectoryRVAdapter extends RecyclerView.Adapter<DirectoryRVAdapter.ViewHolder> {
    private List<File> dirs = new ArrayList<>();
    private File selectedDir;

    private DirectorySelectedCallback directorySelectedCallback;

    private View backActionView;
    private TextView parentFileName;
    private Button selectBtn;

    private SimpleDateFormat simpleDateFormat;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public PastPaperItemView root;

        public ViewHolder(PastPaperItemView root) {
            super(root);
            this.root = root;
        }
    }

    public DirectoryRVAdapter(View root, DirectorySelectedCallback directorySelectedCallback) {
        this.directorySelectedCallback = directorySelectedCallback;
        this.backActionView = root.findViewById(R.id.back);
        this.parentFileName = (TextView) root.findViewById(R.id.parentDirName);
        this.selectBtn = (Button) root.findViewById(R.id.select);

        this.simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PastPaperItemView root = (PastPaperItemView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_past_paper, parent, false);
        root.getIcon().setImageResource(R.drawable.folder);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final File dir = dirs.get(position);

        holder.root.setOnClickListener(v -> loadDir(dir));

        holder.root.getCheckBox().setEnabled(selectedDir == null ? true : selectedDir.equals(dir));
        holder.root.getCheckBox().setChecked(dir.equals(selectedDir)); //Without this,the checkbox disappears,bug?
        holder.root.getCheckBox().setOnClickListener(v -> {
            selectedDir = ((CheckBox) v).isChecked() ? dir : null;
            notifyDataSetChanged();//enable or disable other checkboxes
        });

        holder.root.getTitle().setText(dir.getName());
        holder.root.getDescription().setText(this.simpleDateFormat.format(new Date(dir.lastModified())));
    }

    public void loadDir(final File directory) {
        File[] loadedDirs = directory.listFiles(file -> file.isDirectory() && file.canWrite());

        if (loadedDirs != null && loadedDirs.length != 0) {
            selectedDir = null;
            dirs = Arrays.asList(loadedDirs);
            this.parentFileName.setText(directory.getAbsolutePath());
            this.backActionView.setOnClickListener(v -> {
                if (!directory.equals(new File("/"))) {
                    loadDir(directory.getParentFile());
                }
            });
            this.selectBtn.setOnClickListener(v -> {
                if (selectedDir != null) {
                    directorySelectedCallback.onDirSelected(selectedDir.getAbsolutePath());
                }
            });
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return dirs.size();
    }
}
