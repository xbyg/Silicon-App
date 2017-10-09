package com.xbyg_plus.silicon.fragment.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.utils.ItemSelector;

public class DownloadedItemView extends RelativeLayout implements ItemSelector.SelectableItem {
    public CheckBox checkBox;
    public ImageView icon;
    public TextView title;
    public TextView description;
    public ImageView view;

    public DownloadedItemView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_file, this, true);
        checkBox = findViewById(R.id.checkbox);
        icon = findViewById(R.id.icon);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        view = findViewById(R.id.view);
    }

    @Override
    public void onSelected() {
        checkBox.setChecked(true);
    }

    @Override
    public void onDeselected() {
        checkBox.setChecked(false);
    }
}
