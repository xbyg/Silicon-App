package com.xbyg_plus.silicon.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.dialog.DirectorySelectorDialog;
import com.xbyg_plus.silicon.database.CachesDatabase;
import com.xbyg_plus.silicon.utils.DownloadManager;

import java.io.File;

public class SettingsFragment extends PreferenceFragmentCompat implements DialogManager.DialogHolder {
    private DirectorySelectorDialog directorySelectorDialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        DialogManager.registerDialogHolder(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        Preference savingPath = findPreference("savingPath");
        Preference caches = findPreference("caches");

        savingPath.setSummary(preferences.getString("savingPath", "/sdcard/"));
        caches.setSummary(CachesDatabase.getCachesSize() + " kb");

        savingPath.setOnPreferenceClickListener((Preference preference) -> {
            directorySelectorDialog.setOnDirectorySelectedConsumer(dir -> {
                savingPath.setSummary(dir.getAbsolutePath());
                preferences.edit().putString("savingPath", dir.getAbsolutePath()).apply();
                DownloadManager.setSavePath(dir.getAbsolutePath() + "/");
            }).show(new File("/sdcard/"));
            return true;
        });
        caches.setOnPreferenceClickListener((Preference preference) -> {
            CachesDatabase.clear();
            caches.setSummary("0 kb");
            return true;
        });
    }

    @Override
    public void onDialogsCreated(DialogManager dialogManager) {
        this.directorySelectorDialog = dialogManager.obtain(DirectorySelectorDialog.class);
    }
}
