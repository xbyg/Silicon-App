package com.xbyg_plus.silicon.fragment.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ResDetailsDialog;
import com.xbyg_plus.silicon.model.WebNoticeInfo;
import com.xbyg_plus.silicon.fragment.adapter.infoloader.WebNoticesInfoLoader;
import com.xbyg_plus.silicon.database.CachesDatabase;
import com.xbyg_plus.silicon.utils.ViewIntent;
import com.xbyg_plus.silicon.fragment.adapter.item.NoticeItemView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class NoticeRVAdapter extends WebResourceRVAdapter<WebNoticeInfo, WebNoticesInfoLoader> {
    private int effective = 0; //0: show all,1: show effective

    /**
     * For reducing the use of bandwidth,resolve the address of notice when the user click the notice item or the notice is being downloaded.
     * This field is used to count the address being resolved.
     * For more details {@see WebNoticesInfoLoader}
     *
     * @see #startDownload()
     */
    private ResDetailsDialog resDetailsDialog;

    public NoticeRVAdapter(final Activity activity) {
        super(activity);
        this.resDetailsDialog = new ResDetailsDialog(activity);
        this.infoLoader = new WebNoticesInfoLoader(activity);
        this.resourcesList = CachesDatabase.getNoticeList();
        if (this.resourcesList.size() == 0) {
            loadMoreNotices();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NoticeItemView item = (NoticeItemView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        return new ViewHolder(item) {};
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WebNoticeInfo noticeInfo = this.resourcesList.get(position);

        NoticeItemView item = (NoticeItemView) holder.itemView;
        item.getTitle().setText(noticeInfo.getName());

        item.getCheckBox().setChecked(selector.containsValue(noticeInfo));
        item.getCheckBox().setOnClickListener(v -> {
            if (item.getCheckBox().isChecked()) {
                selector.select(item, noticeInfo);
            } else {
                selector.deselect(item);
            }
        });

        item.getAction().setOnClickListener(v -> {
            if (noticeInfo.getDownloadAddress() == null) {
                infoLoader.resolveDownloadAddress(noticeInfo)
                        .subscribe(() -> ViewIntent.view(activity, Uri.parse(noticeInfo.getDownloadAddress()), "application/pdf"),
                                throwable -> Logger.d("1"));
            } else {
                ViewIntent.view(activity, Uri.parse(noticeInfo.getDownloadAddress()), "application/pdf");
            }
        });

        item.setOnClickListener(v -> {
            if (noticeInfo.getDownloadAddress() == null) {
                infoLoader.resolveDownloadAddress(noticeInfo)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> resDetailsDialog.setContent(noticeInfo).show());
            } else {
                resDetailsDialog.setContent(noticeInfo).show();
            }
        });
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
                }, throwable -> {/* IO Exception*/});
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
