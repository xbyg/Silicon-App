package com.xbyg_plus.silicon.utils;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to show an action mode when the user is selecting items.
 */
public class ItemSelector<Item> {
    protected ActionMode mActionMode;
    protected List<Item> selectedItems = new ArrayList<>();

    protected Activity activity;
    protected int menuID;
    protected ActionModeListener listener;

    public interface ActionModeListener {
        void onActionItemClicked(int itemID);

        void onDestroyActionMode();
    }

    public ItemSelector(Activity activity, int menuID) {
        this.activity = activity;
        this.menuID = menuID;
    }

    public void setActionModeListener(ActionModeListener listener) {
        this.listener = listener;
    }

    public void add(Item item) {
        selectedItems.add(item);
        if (mActionMode == null) {
            mActionMode = activity.startActionMode(actionModeCallback);
        }
    }

    public void remove(Item item) {
        selectedItems.remove(item);
        if (selectedItems.size() == 0) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    public boolean contains(Item item) {
        return this.selectedItems.contains(item);
    }

    public void finish() {
        this.mActionMode.finish();
    }

    public List<Item> getSelectedItems() {
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
            if (listener != null) {
                listener.onDestroyActionMode();
            }
            selectedItems.clear();
            mActionMode = null;
        }
    };
}
