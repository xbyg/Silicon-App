package com.xbyg_plus.silicon.dialog;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.adapter.DirectoryRVAdapter;
import com.xbyg_plus.silicon.utils.functions.Consumer;

import java.io.File;

public class DirectorySelectorDialog extends MyDialog {
    private TextView selectBtn;
    private View backActionView;
    private TextView parentFileName;

    private RecyclerView recyclerView;
    private DirectoryRVAdapter adapter = new DirectoryRVAdapter();

    public DirectorySelectorDialog(Context context) {
        super(context);
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_dir_selector, null, false));

        recyclerView = (RecyclerView) container.findViewById(R.id.dir_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        selectBtn = (TextView) container.findViewById(R.id.select);
        parentFileName = (TextView) container.findViewById(R.id.parentDirName);

        backActionView = container.findViewById(R.id.back);
        backActionView.setOnClickListener(v -> {
            File parentDirectory = adapter.getCurrentDirectory().getParentFile();
            if (adapter.loadDir(parentDirectory)) {
                parentFileName.setText(parentDirectory.getAbsolutePath());
            }
        });
    }

    public DirectorySelectorDialog setOnDirectorySelectedConsumer(Consumer<File> consumer) {
        selectBtn.setOnClickListener(v -> {
            if (adapter.getSelectedDir() != null) {
                consumer.accept(adapter.getSelectedDir());
                dismiss();
            }
        });
        return this;
    }

    public void show() {
        super.show();
        adapter.loadDir(Environment.getExternalStorageDirectory());
    }
}
