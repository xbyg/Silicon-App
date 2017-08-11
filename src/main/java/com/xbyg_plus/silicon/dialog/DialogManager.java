package com.xbyg_plus.silicon.dialog;

import android.app.Activity;
import android.app.Dialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * - prevent creating too many dialog objects by reusing dialogs
 * - avoid passing Activity as a constructor parameter just for showing a dialog
 * - some dialogs may need to be shown across all activities (e.g. LoadingDialog)
 */
public class DialogManager {
    private static DialogManager instance;

    private final Set<DialogHolder> holderList = new HashSet<>();
    private final HashMap<Class<? extends Dialog>, Dialog> dialogs = new HashMap<>();

    public static void init() {
        if (instance == null) {
            instance = new DialogManager();
        }
    }

    //let dialog holder to obtain the dialog they want
    public <T extends Dialog> T obtain(Class<T> dialogClass) {
        return (T) dialogs.get(dialogClass);
    }

    /**
     * Recreate dialogs when new activity is created
     */
    public static void provideContext(Activity activity) {
        instance.dialogs.put(LoginDialog.class, new LoginDialog(activity));
        instance.dialogs.put(LoadingDialog.class, new LoadingDialog(activity));
        instance.dialogs.put(ConfirmDialog.class, new ConfirmDialog(activity));
        instance.dialogs.put(ResDetailsDialog.class, new ResDetailsDialog(activity));
        instance.dialogs.put(FilterDialog.class, new FilterDialog(activity));
        instance.dialogs.put(ChangePasswordDialog.class, new ChangePasswordDialog(activity));
        instance.dialogs.put(DirectorySelectorDialog.class, new DirectorySelectorDialog(activity));

        for (DialogHolder holder : instance.holderList) {
            holder.onDialogsCreated(instance); //let the dialog holders reset their dialogs after creating dialogs
        }
        //System.gc()
    }

    public static void registerDialogHolder(DialogHolder dialogHolder) {
        instance.holderList.add(dialogHolder);
        dialogHolder.onDialogsCreated(instance);
    }

    public static void unregisterDialogHolder(DialogHolder dialogHolder) {
        if (instance.holderList.contains(dialogHolder)) {
            instance.holderList.remove(dialogHolder);
        }
    }

    public interface DialogHolder {
        void onDialogsCreated(DialogManager dialogManager);
    }
}