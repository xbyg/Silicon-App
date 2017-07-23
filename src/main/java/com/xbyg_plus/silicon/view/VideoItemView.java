package com.xbyg_plus.silicon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;

public class VideoItemView extends RelativeLayout {
    private ImageView image;
    private TextView title;
    private TextView views;
    private TextView duration;

    public VideoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        image = (ImageView) findViewById(R.id.img);
        title = (TextView) findViewById(R.id.title);
        views = (TextView) findViewById(R.id.views);
        duration = (TextView) findViewById(R.id.duration);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public ImageView getImage() {
        return image;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getViews() {
        return views;
    }

    public TextView getDuration() {
        return duration;
    }
}
