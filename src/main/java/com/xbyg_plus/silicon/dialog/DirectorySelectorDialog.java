package com.xbyg_plus.silicon.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.adapter.DirectoryRVAdapter;
import com.xbyg_plus.silicon.callback.DirectorySelectedCallback;

import java.io.File;

public class DirectorySelectorDialog {
    private AlertDialog dialog;

    private RecyclerView recyclerView;
    private DirectoryRVAdapter adapter;

    public DirectorySelectorDialog(Context context, final DirectorySelectedCallback callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_dir_selector,null,false);
        dialog = builder.setView(root).create();
        recyclerView = (RecyclerView)root.findViewById(R.id.dir_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new DirectoryRVAdapter(root,dir -> {
            dialog.dismiss();
            callback.onDirSelected(dir);
        });
        recyclerView.setAdapter(adapter);
    }

    public void show(File initDir){
        dialog.show();
        adapter.loadDir(initDir);
    }
}
