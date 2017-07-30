package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebNoticeInfo;
import com.xbyg_plus.silicon.model.WebPastPaperInfo;

public class ResDetailsDialog extends Dialog {
    private LinearLayout layoutForNoticeDetails;
    private LinearLayout layoutForPastPaperDetails;

    protected ResDetailsDialog(Context context) {
        super(context);
        this.setCanceledOnTouchOutside(true);
        layoutForNoticeDetails = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_notice_details, null);
        layoutForPastPaperDetails = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_pastpaper_details, null);
    }

    public ResDetailsDialog setContent(WebNoticeInfo noticeInfo) {
        setContentView(layoutForNoticeDetails);

        ((TextView) findViewById(R.id.name)).setText(getContext().getString(R.string.res_details_name, noticeInfo.getName()));
        ((TextView) findViewById(R.id.startDate)).setText(getContext().getString(R.string.res_details_start_date, noticeInfo.getStartDate()));
        ((TextView) findViewById(R.id.effectiveDate)).setText(getContext().getString(R.string.res_details_effective_date, noticeInfo.getEffectiveDate()));
        ((TextView) findViewById(R.id.uploader)).setText(getContext().getString(R.string.res_details_uploader, noticeInfo.getUploader()));
        ((TextView) findViewById(R.id.download)).setText(getContext().getString(R.string.res_details_download_address, noticeInfo.getDownloadAddress()));
        findViewById(R.id.ok).setOnClickListener(v -> {
            dismiss();
        });
        return this;
    }

    public ResDetailsDialog setContent(WebPastPaperInfo pastPaperInfo) {
        setContentView(layoutForPastPaperDetails);

        ((TextView) findViewById(R.id.name)).setText(getContext().getString(R.string.res_details_name, pastPaperInfo.getName()));
        ((TextView) findViewById(R.id.size)).setText(getContext().getString(R.string.res_details_size, pastPaperInfo.getSize()));
        ((TextView) findViewById(R.id.date)).setText(getContext().getString(R.string.res_details_date, pastPaperInfo.getDate()));
        ((TextView) findViewById(R.id.download)).setText(getContext().getString(R.string.res_details_download_address, pastPaperInfo.getDownloadAddress()));
        findViewById(R.id.ok).setOnClickListener(v -> {
            dismiss();
        });
        return this;
    }
}
