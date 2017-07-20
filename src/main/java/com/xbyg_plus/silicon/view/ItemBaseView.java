package com.xbyg_plus.silicon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;

public abstract class ItemBaseView extends ViewGroup {
    //As the name implies,this base view is aimed to provide a normal item view which can customize checkbox,icon,title,description and action(the right ImageView).
    protected CheckBox checkBox;
    protected ImageView icon;
    protected TextView title;
    protected TextView description;
    protected ImageView action;

    protected int iconSize;
    protected int actionSize;

    protected boolean showCheckBox;
    protected boolean showDescription;
    protected boolean showAction;

    public void test() {

    }

    public ItemBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ItemBaseView, 0, 0);

        this.iconSize = (int) ta.getDimension(R.styleable.ItemBaseView_iconSize, 128);
        this.actionSize = (int) ta.getDimension(R.styleable.ItemBaseView_actionSize, 64);

        this.showCheckBox = ta.getBoolean(R.styleable.ItemBaseView_showCheckBox, true);
        this.showDescription = ta.getBoolean(R.styleable.ItemBaseView_showDescription, true);
        this.showAction = ta.getBoolean(R.styleable.ItemBaseView_showAction, true);

        this.checkBox = new CheckBox(context);

        this.icon = new ImageView(context);
        this.icon.setLayoutParams(new LayoutParams(iconSize, iconSize));
        this.icon.setImageResource(ta.getResourceId(R.styleable.ItemBaseView_iconSrc, R.drawable.file));

        this.title = new TextView(context);
        this.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        this.description = new TextView(context);
        this.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        if (showAction) {
            int maxWidth = getResources().getDimensionPixelSize(R.dimen._190sdp);
            this.title.setMaxWidth(maxWidth);
            this.description.setMaxWidth(maxWidth);
        }

        this.action = new ImageView(context);
        this.action.setLayoutParams(new LayoutParams(actionSize, actionSize));
        this.action.setImageResource(ta.getResourceId(R.styleable.ItemBaseView_actionSrc, R.drawable.arrow));

        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(this.checkBox);
        addView(this.icon);
        addView(this.title);
        addView(this.description);
        addView(this.action);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int h = Math.max(this.icon.getMeasuredHeight(), this.title.getMeasuredHeight() + this.description.getMeasuredHeight()) + 30;
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int leftOffset = l;

        if (showCheckBox) {
            int checkboxLeft = leftOffset + 30;
            int checkboxRight = checkboxLeft + checkBox.getMeasuredWidth();
            int checkboxTop = (getMeasuredHeight() - checkBox.getMeasuredHeight()) / 2;
            checkBox.layout(checkboxLeft, checkboxTop, checkboxRight, checkboxTop + checkBox.getMeasuredHeight());
            leftOffset = checkboxRight;
        }

        int iconLeft = leftOffset + 10;
        int iconRight = iconLeft + icon.getMeasuredWidth();
        int iconTop = (getMeasuredHeight() - icon.getMeasuredHeight()) / 2;
        icon.layout(iconLeft, iconTop, iconRight, iconTop + icon.getMeasuredHeight());
        leftOffset = iconRight;

        if (showDescription) {
            int textHeight = title.getMeasuredHeight() + description.getMeasuredHeight();

            int titleTop = (getMeasuredHeight() - textHeight) / 2;
            int titleLeft = leftOffset + 15;
            int titleRight = titleLeft + title.getMeasuredWidth();
            title.layout(titleLeft, titleTop, titleRight, titleTop + title.getMeasuredHeight());

            int desTop = titleTop + title.getMeasuredHeight();
            int desLeft = titleLeft;
            int desRight = desLeft + description.getMeasuredWidth();
            description.layout(desLeft, desTop, desRight, desTop + description.getMeasuredHeight());
            //leftOffset = Math.max(titleRight,desRight);
        } else {
            int titleTop = (getMeasuredHeight() - title.getMeasuredHeight()) / 2;
            int titleLeft = leftOffset + 15;
            int titleRight = titleLeft + title.getMeasuredWidth();
            title.layout(titleLeft, titleTop, titleRight, titleTop + title.getMeasuredHeight());
            //leftOffset = titleRight;
        }

        if (showAction) {
            double actionRight = getMeasuredWidth() * 0.95;
            double actionLeft = actionRight - action.getMeasuredWidth();
            int actionTop = (getMeasuredHeight() - action.getMeasuredHeight()) / 2;
            action.layout((int) actionLeft, actionTop, (int) actionRight, actionTop + action.getMeasuredHeight());
        }
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public ImageView getIcon() {
        return icon;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getDescription() {
        return description;
    }

    public ImageView getAction() {
        return action;
    }
}
