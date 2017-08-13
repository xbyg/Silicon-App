package com.xbyg_plus.silicon.fragment.adapter.item;

import android.content.Context;
import android.util.AttributeSet;

import com.xbyg_plus.silicon.utils.ItemSelector;

public abstract class SelectableItemView extends ItemBaseView implements ItemSelector.SelectableItem{

    public SelectableItemView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public SelectableItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onSelected() {
        this.checkBox.setChecked(true);
    }

    @Override
    public void onDeselected() {
        this.checkBox.setChecked(false);
    }
}
