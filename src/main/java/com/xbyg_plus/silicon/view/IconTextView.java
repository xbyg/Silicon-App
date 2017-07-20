package com.xbyg_plus.silicon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;

public class IconTextView extends ViewGroup {
    private ImageView icon;
    private TextView text;

    public boolean center = false;
    public int icon_margin_text = 20;

    public IconTextView(Context context, int iconID, int icon_size, String text) {
        super(context);
        this.icon = new ImageView(getContext());
        this.text = new TextView(getContext());
        this.icon.setImageDrawable(ContextCompat.getDrawable(getContext(), iconID));
        this.icon.setLayoutParams(new LinearLayout.LayoutParams(icon_size, icon_size));
        this.text.setText(text);
        this.addView(this.icon);
        this.addView(this.text);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IconTextView, 0, 0);
        center = ta.getBoolean(R.styleable.IconTextView_center, false);
        icon_margin_text = (int) ta.getDimension(R.styleable.IconTextView_icon_margin_text, 20);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.icon = (ImageView) getChildAt(0);
        this.text = (TextView) getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int h = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY ? MeasureSpec.getSize(heightMeasureSpec) : icon.getMeasuredHeight();
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (center) {
            int iconLeft = (getMeasuredWidth() - icon.getMeasuredWidth() - icon_margin_text - text.getMeasuredWidth()) / 2;
            int iconTop = (getMeasuredHeight() - icon.getMeasuredHeight()) / 2;
            icon.layout(iconLeft, iconTop, iconLeft + icon.getMeasuredWidth(), iconTop + icon.getMeasuredHeight());

            int textLeft = iconLeft + icon.getMeasuredWidth() + icon_margin_text;
            int textTop = (getMeasuredHeight() - text.getMeasuredHeight()) / 2;
            text.layout(textLeft, textTop, textLeft + text.getMeasuredWidth(), textTop + text.getMeasuredHeight());
        } else {
            int iconTop = (getMeasuredHeight() - icon.getMeasuredHeight()) / 2;
            icon.layout(0, iconTop, icon.getMeasuredWidth(), iconTop + icon.getMeasuredHeight());

            int textLeft = icon.getMeasuredWidth() + icon_margin_text;
            int textTop = (getMeasuredHeight() - text.getMeasuredHeight()) / 2;
            text.layout(textLeft, textTop, textLeft + text.getMeasuredWidth(), textTop + text.getMeasuredHeight());
        }
    }

    public ImageView getIcon() {
        return icon;
    }

    public TextView getText() {
        return text;
    }
}
