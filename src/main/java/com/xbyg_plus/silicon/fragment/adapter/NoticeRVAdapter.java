package com.xbyg_plus.silicon.fragment.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.dialog.ResDetailsDialog;
import com.xbyg_plus.silicon.model.WebNoticeInfo;
import com.xbyg_plus.silicon.fragment.adapter.infoloader.WebNoticesInfoLoader;
import com.xbyg_plus.silicon.database.CachesDatabase;
import com.xbyg_plus.silicon.utils.ViewIntent;
import com.xbyg_plus.silicon.fragment.adapter.item.NoticeItemView;

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

    private ResDetailsDialog resDetailsDialog;

    public NoticeRVAdapter(final Activity activity) {
        super(activity);
        this.infoLoader = new WebNoticesInfoLoader();
        this.resourcesList = CachesDatabase.getNoticeList();
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
                infoLoader.resolveDownloadAddress(noticeInfo, () -> {
                    Uri uri = Uri.parse(noticeInfo.getDownloadAddress());
                    ViewIntent.view(activity, uri, "application/pdf");
                });
            } else {
                Uri uri = Uri.parse(noticeInfo.getDownloadAddress());
                ViewIntent.view(activity, uri, "application/pdf");
            }
        });

        item.setOnClickListener(v -> {
            if (noticeInfo.getDownloadAddress() == null) {
                infoLoader.resolveDownloadAddress(noticeInfo, () -> {
                    activity.runOnUiThread(() -> resDetailsDialog.setContent(noticeInfo).show());
                });
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
        this.infoLoader.request(reqParams, parsedList -> {
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
    protected void showDownloadConfirm() {
        for (WebNoticeInfo noticeInfo : selector.getSelectedItems()) {
            if (noticeInfo.getDownloadAddress() == null) {
                noticeAddressCount++;
                infoLoader.resolveDownloadAddress(noticeInfo, () -> {
                    if (noticeAddressCount == selector.getSelectedItems().size()) {
                        noticeAddressCount = 0;
                        activity.runOnUiThread(() -> NoticeRVAdapter.super.showDownloadConfirm());
                    }
                });
            }
        }

        //if no notice is required to resolve its address then show the download confirm directly
        if (noticeAddressCount == 0) {
            super.showDownloadConfirm();
        }
    }

    @Override
    public void onDialogsCreated(DialogManager dialogManager) {
        super.onDialogsCreated(dialogManager);
        this.resDetailsDialog = dialogManager.obtain(ResDetailsDialog.class);
    }
}
