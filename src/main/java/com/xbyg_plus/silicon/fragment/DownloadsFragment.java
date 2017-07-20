package com.xbyg_plus.silicon.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ConfirmDialog;
import com.xbyg_plus.silicon.event.DownloadCompleteEvent;
import com.xbyg_plus.silicon.event.DownloadStartEvent;
import com.xbyg_plus.silicon.task.DownloadTask;
import com.xbyg_plus.silicon.utils.DownloadsDatabase;
import com.xbyg_plus.silicon.utils.ItemSelector;
import com.xbyg_plus.silicon.utils.ViewIntent;
import com.xbyg_plus.silicon.view.DownloadedItemView;
import com.xbyg_plus.silicon.view.IconTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadsFragment extends Fragment {
    @BindView(R.id.downloadingPackageLayout)
    LinearLayout downloadingPackageLayout;
    @BindView(R.id.downloadsLayout)
    LinearLayout downloadsLayout;

    private ItemSelector<DownloadedItemView> selector;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.selector = new ItemSelector<>(getActivity(), R.menu.file_delete);
        this.selector.setActionModeListener(new ItemSelector.ActionModeListener() {
            @Override
            public void onActionItemClicked(int itemID) {
                if (itemID == R.id.action_delete) {
                    String nameList = "";
                    for (View v : selector.getSelectedItems()) {
                        nameList += ((File) v.getTag()).getName() + "\n";
                    }
                    new ConfirmDialog(getContext(), getString(R.string.delete_files_confirm), nameList, confirmation -> {
                        if (confirmation) {
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
        IconTextView emptyItem = new IconTextView(getContext(), R.drawable.crying, 96, getString(R.string.empty));
        emptyItem.setTag("empty_item");
        emptyItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 256));
        emptyItem.center = true;
        root.addView(emptyItem);
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
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
