package com.xbyg_plus.silicon.utils;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

/**
 * This class is used to show an action mode when the user is selecting items and store the selected items
 */
public final class ItemSelector<Item extends ItemSelector.SelectableItem, AttachedData> {
    protected ActionMode mActionMode;
    //Each item has an attached data object such as File, WebNoticeInfo, WebPastPaperInfo
    protected HashMap<Item, AttachedData> selectedItems = new HashMap<>();

    protected Activity activity;
    protected int menuID;
    protected ActionItemClickListener listener;

    public interface SelectableItem {
        // give a signal to the view when it is being select or deselect
        void onSelected();

        void onDeselected();
    }

    public interface ActionItemClickListener {
        void onActionItemClicked(int itemID);
    }

    public ItemSelector(Activity activity, int menuID) {
        this.activity = activity;
        this.menuID = menuID;
    }

    public void setActionItemClickListener(ActionItemClickListener listener) {
        this.listener = listener;
    }

    public void select(Item item, AttachedData attachedData) {
        selectedItems.put(item, attachedData);
        item.onSelected();
        if (mActionMode == null) {
            mActionMode = activity.startActionMode(actionModeCallback);
        }
    }

    public void deselect(Item item) {
        selectedItems.remove(item);
        item.onDeselected();
        if (selectedItems.size() == 0) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    public boolean contains(SelectableItem item) {
        return this.selectedItems.containsKey(item);
    }

    public void finish() {
        this.mActionMode.finish();
    }

    public HashMap<Item, AttachedData> getSelectedItems() {
        return this.selectedItems;
    }

    protected ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(menuID, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (listener != null) {
                listener.onActionItemClicked(item.getItemId());
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            for (SelectableItem item : selectedItems.keySet()) {
                item.onDeselected();
            }
            selectedItems.clear();
            mActionMode = null;
        }
    };
}
