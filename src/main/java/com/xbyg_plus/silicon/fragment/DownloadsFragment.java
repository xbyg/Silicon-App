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
import com.xbyg_plus.silicon.data.repository.DownloadRepository;
import com.xbyg_plus.silicon.dialog.ConfirmDialog;
import com.xbyg_plus.silicon.fragment.view.DownloadedItemView;
import com.xbyg_plus.silicon.fragment.view.DownloadingItemView;
import com.xbyg_plus.silicon.task.DownloadTask;
import com.xbyg_plus.silicon.utils.ItemSelector;
import com.xbyg_plus.silicon.utils.ViewIntent;

import java.io.File;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class DownloadsFragment extends Fragment {
    @BindView(R.id.downloadingLayout) LinearLayout downloadingLayout;
    @BindView(R.id.downloadsLayout) LinearLayout downloadsLayout;

    private ConfirmDialog deleteFilesConfirmDialog;
    private ItemSelector<DownloadedItemView, File> selector;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.deleteFilesConfirmDialog = new ConfirmDialog(getActivity()).setOnConfirmConsumer(confirm -> {
            if (confirm) {
                for (Map.Entry<DownloadedItemView, File> entry : selector.getSelectedItems().entrySet()) {
                    File file = entry.getValue();
                    file.delete();
                    DownloadRepository.instance.deleteSingle(file).subscribe();
                    downloadsLayout.removeView(entry.getKey());
                }
                if (downloadsLayout.getChildCount() == 1) {
                    addEmptyItem(downloadsLayout);
                }
                new AlertDialog.Builder(getContext()).setTitle(getString(R.string.done)).setMessage(getString(R.string.file_deleted)).create().show();
            }
            selector.finish();
        });

        this.selector = new ItemSelector<>(getActivity(), R.menu.file_delete);
        this.selector.setActionItemClickListener(itemID -> {
            if (itemID == R.id.action_delete) {
                String nameList = "";
                for (File file : selector.getSelectedItems().values()) {
                    nameList += file.getName() + "\n";
                }

                deleteFilesConfirmDialog.setContent(getString(R.string.delete_files_confirm), nameList).show();
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

        addEmptyItem(downloadsLayout);
        addEmptyItem(downloadingLayout);

        DownloadRepository.instance.getData(true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(files -> {
                    for (File file : files) {
                        addDownloadedItem(file);
                    }
                });

        this.observeDownloadTask();
    }

    private void observeDownloadTask() {
        DownloadTask.pool.subscribe(task -> {
            DownloadingItemView view = (DownloadingItemView) LayoutInflater.from(getContext()).inflate(R.layout.item_downloading, null, false);

            task.getProgressObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(progress -> view.progressText.setText(progress + "%"));

            task.getResultObservable()
                    .doOnSubscribe(disposable -> {
                        this.removeEmptyItem(downloadingLayout);

                        view.title.setText(task.getResInfo().getName());
                        view.cancel.setOnClickListener(v -> {
                            disposable.dispose();

                            downloadingLayout.removeView(view);
                            if (this.downloadingLayout.getChildCount() == 1) { //the first child is the title view
                                addEmptyItem(downloadingLayout);
                            }
                        });
                        this.downloadingLayout.addView(view, 1);
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(file -> {
                        this.addDownloadedItem(file);

                        this.downloadingLayout.removeView(view);
                        if (this.downloadingLayout.getChildCount() == 1) { //the first child is the title view
                            addEmptyItem(downloadingLayout);
                        }
                    });
        });
    }

    private void addDownloadedItem(File file) {
        this.removeEmptyItem(downloadsLayout);

        DownloadedItemView view = new DownloadedItemView(getContext());

        //TODO: different icon for different file type
        view.icon.setImageResource(R.drawable.file);
        view.title.setText(file.getName());
        view.description.setText(file.getParent());
        view.checkBox.setOnClickListener(v -> {
            //OnCheckedChangedListener conflicts with setChecked() function in onDestroyActionMode() function,since they operate the HashMap(selectedItems) at the same time
            if (view.checkBox.isChecked()) {
                selector.select(view, file);
            } else {
                selector.deselect(view);
            }
        });

        view.setOnClickListener(v -> ViewIntent.view(getActivity(), Uri.fromFile(file)));

        this.downloadsLayout.addView(view, 1);
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
}
