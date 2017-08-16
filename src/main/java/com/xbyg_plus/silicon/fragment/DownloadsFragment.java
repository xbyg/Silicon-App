package com.xbyg_plus.silicon.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ConfirmDialog;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.task.DownloadTask;
import com.xbyg_plus.silicon.database.DownloadsDatabase;
import com.xbyg_plus.silicon.utils.DownloadManager;
import com.xbyg_plus.silicon.utils.ItemSelector;
import com.xbyg_plus.silicon.utils.ViewIntent;
import com.xbyg_plus.silicon.fragment.adapter.item.DownloadedItemView;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadsFragment extends Fragment implements DialogManager.DialogHolder, DownloadManager.DownloadTaskListener {
    @BindView(R.id.downloadingLayout) LinearLayout downloadingLayout;
    @BindView(R.id.downloadsLayout) LinearLayout downloadsLayout;

    private ConfirmDialog deleteFilesConfirmDialog;
    private ItemSelector<DownloadedItemView, File> selector;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.selector = new ItemSelector<>(getActivity(), R.menu.file_delete);
        this.selector.setActionItemClickListener(itemID -> {
            if (itemID == R.id.action_delete) {
                String nameList = "";
                for (File file : selector.getSelectedItems().values()) {
                    nameList += file.getName() + "\n";
                }

                deleteFilesConfirmDialog.setContent(getString(R.string.delete_files_confirm), nameList)
                        .setOnConfirmConsumer(confirm -> {
                            if (confirm) {
                                for (Map.Entry<DownloadedItemView, File> entry : selector.getSelectedItems().entrySet()) {
                                    File file = entry.getValue();
                                    DownloadsDatabase.removeDownloadPath(file.getName());
                                    file.delete();
                                    downloadsLayout.removeView(entry.getKey());
                                }
                                DownloadsDatabase.save();
                                new AlertDialog.Builder(getContext()).setTitle(getString(R.string.done)).setMessage(getString(R.string.file_deleted)).create().show();
                            }
                            selector.finish();
                        }).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_downloads, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, null);
        ButterKnife.bind(this, view);
        DialogManager.registerDialogHolder(this);
        DownloadManager.setDownloadTaskListener(this);

        List<DownloadTask> startedTasks = DownloadManager.getStartedTasks();
        if (startedTasks.size() == 0) {
            addEmptyItem(downloadingLayout);
        } else {
            for (DownloadTask task : startedTasks) {
                this.downloadingLayout.addView(task.getAttachedView(), 1);
            }
        }

        List<File> files = DownloadsDatabase.getDownloads();
        if (files.size() == 0) {
            addEmptyItem(downloadsLayout);
        } else {
            for (File file : files) {
                addDownloadedItem(file);
            }
        }
    }

    @Override
    public void onDownloadStart(DownloadTask task) {
        this.removeEmptyItem(downloadingLayout);
        this.downloadingLayout.addView(task.getAttachedView(), 1);
    }

    @Override
    public void onDownloadFinish(DownloadTask task, File file) {
        this.removeEmptyItem(downloadsLayout);
        this.addDownloadedItem(file);

        this.downloadingLayout.removeView(task.getAttachedView());
        if (this.downloadingLayout.getChildCount() == 1) { //the first child is the title view
            addEmptyItem(downloadingLayout);
        }
    }

    @Override
    public void onDownloadError(Throwable throwable) {
        Logger.d(throwable.getMessage());
    }

    private void addDownloadedItem(File file) {
        DownloadedItemView root = (DownloadedItemView) LayoutInflater.from(getContext()).inflate(R.layout.item_downloaded_file, null, false);
        root.setTag(file);

        //TODO: different icon for different file type
        root.getTitle().setText(file.getName());
        root.getDescription().setText(file.getParent());
        root.getCheckBox().setOnClickListener(v -> {
            //OnCheckedChangedListener conflicts with setChecked() function in onDestroyActionMode() function,since they operate the HashMap(selectedItems) at the same time
            if (root.getCheckBox().isChecked()) {
                selector.select(root, file);
            } else {
                selector.deselect(root);
            }
        });
        root.setOnClickListener(v -> ViewIntent.view(getActivity(), Uri.fromFile(file)));

        this.downloadsLayout.addView(root, 1);
    }

    private void addEmptyItem(LinearLayout root) {
        LinearLayout container = new LinearLayout(getContext());
        container.setTag("empty_item");
        container.setGravity(Gravity.CENTER);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 256));

        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(96, 96));
        imageView.setImageResource(R.drawable.crying);

        TextView textView = new TextView(getContext());
        textView.setText(getString(R.string.empty));
        textView.setTextSize(20);

        container.addView(imageView);
        container.addView(textView);
        root.addView(container);
    }

    private void removeEmptyItem(LinearLayout root) {
        View emptyItem = root.findViewWithTag("empty_item");
        if (emptyItem != null) {
            root.removeView(emptyItem);
        }
    }

    @Override
    public void onDialogsCreated(DialogManager dialogManager) {
        this.deleteFilesConfirmDialog = dialogManager.obtain(ConfirmDialog.class);
    }
}
