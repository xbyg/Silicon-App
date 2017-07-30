package com.xbyg_plus.silicon.fragment.adapter.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadingItemView extends ViewGroup {
    private ProgressBar progressBar;
    private TextView title;
    private TextView progress;
    private ImageView cancel;

    public DownloadingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.progressBar = (ProgressBar) getChildAt(0);
        this.title = (TextView) getChildAt(1);
        this.progress = (TextView) getChildAt(2);
        this.cancel = (ImageView) getChildAt(3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int h = Math.max(this.progressBar.getMeasuredHeight(), this.title.getMeasuredHeight() + this.progress.getMeasuredHeight()) + 30;
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int progressBarTop = (getMeasuredHeight() - progressBar.getMeasuredHeight()) / 2;
        int progressBarLeft = 15;
        int progressBarRight = progressBarLeft + progressBar.getMeasuredWidth();
        progressBar.layout(progressBarLeft, progressBarTop, progressBarRight, progressBarTop + progressBar.getMeasuredHeight());

        int textLeft = progressBarRight + 15;
        int titleTop = (getMeasuredHeight() - title.getMeasuredHeight() - progress.getMeasuredHeight()) / 2;
        int titleBottom = titleTop + title.getMeasuredHeight();
        title.layout(textLeft, titleTop, textLeft + title.getMeasuredWidth(), titleBottom);

        int progressTop = titleBottom + 5;
        progress.layout(textLeft, progressTop, textLeft + progress.getMeasuredWidth(), progressTop + progress.getMeasuredHeight());

        int cancelRight = (int) (getMeasuredWidth() * 0.95);
        int cancelLeft = cancelRight - cancel.getMeasuredWidth();
        int cancelTop = (getMeasuredHeight() - cancel.getMeasuredHeight()) / 2;
        cancel.layout(cancelLeft, cancelTop, cancelRight, cancelTop + cancel.getMeasuredHeight());
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getProgress() {
        return progress;
    }

    public ImageView getCancel() {
        return cancel;
    }
}
