package com.xbyg_plus.silicon.fragment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;

public class DownloadingItemView extends RelativeLayout {
    public ProgressBar progressBar;
    public TextView title;
    public TextView progressText;
    public ImageView cancel;

    public DownloadingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        progressBar = findViewById(R.id.progress_bar);
        title = findViewById(R.id.title);
        progressText = findViewById(R.id.progress_text);
        cancel = findViewById(R.id.delete);
    }
}