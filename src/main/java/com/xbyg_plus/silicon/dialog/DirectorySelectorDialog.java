package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.adapter.DirectoryRVAdapter;

import java.io.File;

public class DirectorySelectorDialog extends Dialog {
    private DirectorySelectedCallback callback;

    private RecyclerView recyclerView;
    private DirectoryRVAdapter adapter;

    public interface DirectorySelectedCallback {
        void onDirSelected(String dir);
    }

    protected DirectorySelectorDialog(Context context) {
        super(context);
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_dir_selector, null, false);
        setContentView(root);
        recyclerView = (RecyclerView) root.findViewById(R.id.dir_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new DirectoryRVAdapter(root, dir -> {
            callback.onDirSelected(dir);
            dismiss();
        });
        recyclerView.setAdapter(adapter);
    }

    public DirectorySelectorDialog setDirectorySelectedCallback(DirectorySelectedCallback callback) {
        if (callback != null) {
            this.callback = callback;
        }
        return this;
    }

    public void show(File initDir) {
        super.show();
        adapter.loadDir(initDir);
    }
}
