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

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ConfirmDialog;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.event.DownloadCompleteEvent;
import com.xbyg_plus.silicon.event.DownloadStartEvent;
import com.xbyg_plus.silicon.task.DownloadTask;
import com.xbyg_plus.silicon.database.DownloadsDatabase;
import com.xbyg_plus.silicon.utils.ItemSelector;
import com.xbyg_plus.silicon.utils.ViewIntent;
import com.xbyg_plus.silicon.fragment.adapter.item.DownloadedItemView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadsFragment extends Fragment implements DialogManager.DialogHolder {
    @BindView(R.id.downloadingPackageLayout) LinearLayout downloadingPackageLayout;
    @BindView(R.id.downloadsLayout) LinearLayout downloadsLayout;

    private ConfirmDialog deleteFilesConfirmDialog;
    private ItemSelector<DownloadedItemView> selector;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.selector = new ItemSelector<>(getActivity(), R.menu.file_delete);
        this.selector.setActionModeListener(new ItemSelector.ActionModeListener() {
            @Override
            public void onActionItemClicked(int itemID) {
                if (itemID == R.id.action_delete) {
                    deleteFilesConfirmDialog.confirmObservable()
                            .subscribe(confirm -> {
                                if (confirm) {
                                    for (View v : selector.getSelectedItems()) {
                                        DownloadsDatabase.removeDownloadPath(((File) v.getTag()).getName());
                                        ((File) v.getTag()).delete();
                                        downloadsLayout.removeView(v);
                                    }
                                    DownloadsDatabase.save();
                                    new AlertDialog.Builder(getContext()).setTitle(getString(R.string.done)).setMessage(getString(R.string.file_deleted)).create().show();
                                }
                                selector.finish();
                            });

                    String nameList = "";
                    for (View v : selector.getSelectedItems()) {
                        nameList += ((File) v.getTag()).getName() + "\n";
                    }
                    deleteFilesConfirmDialog
                            .setContent(getString(R.string.delete_files_confirm), nameList)
                            .show();
                }
            }

            @Override
            public void onDestroyActionMode() {
                for (DownloadedItemView v : selector.getSelectedItems()) {
                    v.getCheckBox().setChecked(false);
                }
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
        EventBus.getDefault().register(this);
        DialogManager.registerDialogHolder(this);

        List<DownloadTask> downloadTasks = DownloadTask.pool;
        if (downloadTasks.size() == 0) {
            addEmptyItem(downloadingPackageLayout);
        } else {
            for (DownloadTask downloadTask : downloadTasks) {
                if (downloadTask.getAttachedView().getParent() == null) {
                    this.downloadingPackageLayout.addView(downloadTask.getAttachedView(), 1);
                }
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

    private void addDownloadedItem(File file) {
        DownloadedItemView root = (DownloadedItemView) LayoutInflater.from(getContext()).inflate(R.layout.item_downloaded_file, null, false);
        root.setTag(file);

        //TODO: different icon for different file type
        root.getTitle().setText(file.getName());
        root.getDescription().setText(file.getParent());
        root.getCheckBox().setOnClickListener(v -> {
            //OnCheckedChangedListener conflicts with setChecked() function in onDestroyActionMode() function,since they operate the HashMap(selectedItems) at the same time
            if (root.getCheckBox().isChecked()) {
                selector.add(root);
            } else {
                selector.remove(root);
            }
        });
        root.setOnClickListener(v -> {
            Uri uri = Uri.fromFile(file);
            ViewIntent.view(getActivity(), uri);
        });

        getActivity().runOnUiThread(() -> this.downloadsLayout.addView(root, 1));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadStart(DownloadStartEvent event) {
        if (event.getDownloadTask().getAttachedView().getParent() == null) {
            this.downloadingPackageLayout.addView(event.getDownloadTask().getAttachedView(), 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadComplete(DownloadCompleteEvent event) {
        View emptyItem = this.downloadsLayout.findViewWithTag("empty_item");
        if (emptyItem != null) {
            this.downloadsLayout.removeView(emptyItem);
        }
        this.addDownloadedItem(event.getFile());

        this.downloadingPackageLayout.removeView(event.getDownloadTask().getAttachedView());
        if (this.downloadingPackageLayout.getChildCount() == 1) { //the first child is the title view
            addEmptyItem(downloadingPackageLayout);
        }
    }

    @Override
    public void onDialogsCreated(DialogManager dialogManager) {
        this.deleteFilesConfirmDialog = dialogManager.obtain(ConfirmDialog.class);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
