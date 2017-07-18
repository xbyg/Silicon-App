package com.xbyg_plus.silicon.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebNoticeInfo;
import com.xbyg_plus.silicon.model.WebPastPaperInfo;
import com.xbyg_plus.silicon.model.WebResourceInfo;

public class ResDetailsDialog {
    private Dialog dialog;
    private RelativeLayout layout;

    public ResDetailsDialog(Context context,WebResourceInfo resInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if(resInfo instanceof WebPastPaperInfo){
            layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.dialog_pastpaper_details,null);
            ((TextView)layout.findViewById(R.id.name)).setText(context.getString(R.string.name)+": "+resInfo.getName());
            ((TextView)layout.findViewById(R.id.size)).setText(context.getString(R.string.size)+": "+resInfo.getSize()+"kb");
            ((TextView)layout.findViewById(R.id.date)).setText(context.getString(R.string.date)+": "+ resInfo.getDate());
            ((TextView)layout.findViewById(R.id.download)).setText(context.getString(R.string.download_address)+": "+resInfo.getDownloadAddress());
        }else if(resInfo instanceof WebNoticeInfo){
            layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.dialog_notice_details,null);
            ((TextView)layout.findViewById(R.id.name)).setText(context.getString(R.string.name)+": "+ resInfo.getName());
            ((TextView)layout.findViewById(R.id.startDate)).setText(context.getString(R.string.start_date)+": "+ ((WebNoticeInfo) resInfo).getStartDate());
            ((TextView)layout.findViewById(R.id.effectiveDate)).setText(context.getString(R.string.effective_date)+": "+ ((WebNoticeInfo) resInfo).getEffectiveDate());
            ((TextView)layout.findViewById(R.id.uploader)).setText(context.getString(R.string.uploader)+": "+ ((WebNoticeInfo) resInfo).getUploader());
            ((TextView)layout.findViewById(R.id.download)).setText(context.getString(R.string.download_address)+": "+resInfo.getDownloadAddress());
        }

        builder.setView(layout);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        layout.findViewById(R.id.ok).setOnClickListener(v->{dialog.dismiss();});
    }

    public void show(){
        this.dialog.show();
    }
}
