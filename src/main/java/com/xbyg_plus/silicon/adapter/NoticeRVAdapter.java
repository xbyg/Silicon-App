package com.xbyg_plus.silicon.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ResDetailsDialog;
import com.xbyg_plus.silicon.model.WebNoticeInfo;
import com.xbyg_plus.silicon.infoloader.WebNoticesInfoLoader;
import com.xbyg_plus.silicon.infoloader.WebResourcesInfoLoader;
import com.xbyg_plus.silicon.utils.CachesDatabase;
import com.xbyg_plus.silicon.utils.ViewIntent;
import com.xbyg_plus.silicon.view.NoticeItemView;

import java.util.List;

public class NoticeRVAdapter extends WebResourceRVAdapter<WebNoticeInfo, WebNoticesInfoLoader> {
    private int effective = 0; //0: show all,1: show effective

    /**
     * For reducing the use of bandwidth,resolve the address of notice when the user click the notice item or the notice is being downloaded.
     * This field is used to count the address being resolved.
     * For more details {@see WebNoticesInfoLoader}
     *
     * @see #showDownloadConfirm()
     */
    private int noticeAddressCount = 0;

    public NoticeRVAdapter(final Activity activity) {
        super(activity);
        this.infoLoader = new WebNoticesInfoLoader(activity);
        this.resourcesList = CachesDatabase.noticeList;
        if (this.resourcesList.size() == 0) {
            loadMoreNotices();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NoticeItemView item = (NoticeItemView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WebNoticeInfo noticeInfo = this.resourcesList.get(position);

        NoticeItemView item = (NoticeItemView) holder.item;
        item.getTitle().setText(noticeInfo.getName());

        item.getCheckBox().setChecked(selector.contains(noticeInfo));
        item.getCheckBox().setOnClickListener(v -> {
            if (item.getCheckBox().isChecked()) {
                selector.add(noticeInfo);
            } else {
                selector.remove(noticeInfo);
            }
        });

        item.getAction().setOnClickListener(v -> {
            if (noticeInfo.getDownloadAddress() == null) {
                infoLoader.resolveDownloadAddress(noticeInfo, new WebNoticesInfoLoader.WebNoticeAddressResolvedCallback() {
                    @Override
                    public void onNoticeAddressResolved() {
                        Uri uri = Uri.parse(noticeInfo.getDownloadAddress());
                        ViewIntent.view(activity, uri, "application/pdf");
                    }
                });
            } else {
                Uri uri = Uri.parse(noticeInfo.getDownloadAddress());
                ViewIntent.view(activity, uri, "application/pdf");
            }
        });

        item.setOnClickListener(v -> {
            if (noticeInfo.getDownloadAddress() == null) {
                infoLoader.resolveDownloadAddress(noticeInfo, new WebNoticesInfoLoader.WebNoticeAddressResolvedCallback() {
                    @Override
                    public void onNoticeAddressResolved() {
                        activity.runOnUiThread(() -> new ResDetailsDialog(activity, noticeInfo).show());
                    }
                });
            } else {
                new ResDetailsDialog(activity, noticeInfo).show();
            }
        });
    }

    public int getPagesLoaded() {
        return this.resourcesList.size() / 30; //one page has 30 pieces of notices
    }

    public void loadMoreNotices() {
        WebNoticesInfoLoader.RequestParams params = new WebNoticesInfoLoader.RequestParams();
        params.page = getPagesLoaded() + 1;
        params.effective = effective;
        this.infoLoader.request(params, new WebResourcesInfoLoader.LoadCallback() {
            @Override
            public void onLoaded(WebResourcesInfoLoader.RequestParameters params, List parsedList) {
                resourcesList.addAll(parsedList);
                updateView();
            }
        });
    }

    @Override
    public void refreshData() {
        this.resourcesList.clear();
        loadMoreNotices();
    }

    @Override
    protected void showDownloadConfirm() {
        for (WebNoticeInfo noticeInfo : selector.getSelectedItems()) {
            if (noticeInfo.getDownloadAddress() == null) {
                noticeAddressCount++;
                infoLoader.resolveDownloadAddress(noticeInfo, new WebNoticesInfoLoader.WebNoticeAddressResolvedCallback() {
                    @Override
                    public void onNoticeAddressResolved() {
                        if (noticeAddressCount == selector.getSelectedItems().size()) {
                            noticeAddressCount = 0;
                            activity.runOnUiThread(() -> NoticeRVAdapter.super.showDownloadConfirm());
                        }
                    }
                });
            }
        }

        //if no notice is required to resolve its address then show the download confirm directly
        if (noticeAddressCount == 0) {
            super.showDownloadConfirm();
        }
    }
}
