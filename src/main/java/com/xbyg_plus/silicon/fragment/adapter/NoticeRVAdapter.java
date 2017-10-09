package com.xbyg_plus.silicon.fragment.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebNoticeInfo;
import com.xbyg_plus.silicon.fragment.adapter.infoloader.WebNoticesInfoLoader;
import com.xbyg_plus.silicon.utils.ItemSelector;
import com.xbyg_plus.silicon.utils.ViewIntent;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public class NoticeRVAdapter extends WebResourceRVAdapter<NoticeRVAdapter.ViewHolder, WebNoticeInfo, WebNoticesInfoLoader> {
    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemSelector.SelectableItem {
        CheckBox checkBox;
        TextView title;
        ImageView view;

        ViewHolder(View root) {
            super(root);
            checkBox = root.findViewById(R.id.checkbox);
            title = root.findViewById(R.id.title);
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

    private int effective = 0; //0: show all,1: show effective

    /**
     * For reducing the use of bandwidth,resolve the address of notice when the user click the notice item or the notice is being downloaded.
     * For more details {@see WebNoticesInfoLoader}
     *
     * @see #startDownload()
     */
    public NoticeRVAdapter(Activity activity, Single<List<WebNoticeInfo>> dataSource) {
        super(activity);
        this.infoLoader = new WebNoticesInfoLoader(activity);
        dataSource.subscribe(notices -> {
            this.resourcesList = notices;
            updateView();
            if (this.resourcesList.size() == 0) {
                loadMoreNotices();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WebNoticeInfo noticeInfo = this.resourcesList.get(position);

        holder.title.setText(noticeInfo.getName());

        holder.checkBox.setChecked(selector.containsValue(noticeInfo));
        holder.checkBox.setOnClickListener(v -> {
            if (holder.checkBox.isChecked()) {
                selector.select(holder, noticeInfo);
            } else {
                selector.deselect(holder);
            }
        });

        holder.view.setOnClickListener(v -> infoLoader.resolveDownloadAddress(noticeInfo).subscribe(() -> ViewIntent.view(activity, Uri.parse(noticeInfo.getDownloadAddress()), "application/pdf")));

        holder.itemView.setOnClickListener(v -> infoLoader.resolveDownloadAddress(noticeInfo).subscribe(() -> ViewIntent.view(activity, Uri.parse(noticeInfo.getDownloadAddress()), "application/pdf")));
    }

    public int getPagesLoaded() {
        return this.resourcesList.size() / 30; //one page has 30 pieces of notices
    }

    public void loadMoreNotices() {
        WebNoticesInfoLoader.RequestParams reqParams = new WebNoticesInfoLoader.RequestParams();
        reqParams.page = getPagesLoaded() + 1;
        reqParams.effective = effective;
        this.infoLoader.request(reqParams)
                .subscribe(parsedList -> {
                    resourcesList.addAll(parsedList);
                    updateView();
                });
    }

    @Override
    public void refreshData() {
        this.resourcesList.clear();
        loadMoreNotices();
    }

    @Override
    protected Observable<WebNoticeInfo> startDownload() {
        return Observable.fromIterable(selector.getSelectedItems().values())
                .flatMapCompletable(infoLoader::resolveDownloadAddress)
                .andThen(super.startDownload());
    }
}
