package com.xbyg_plus.silicon.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.DirectorySelectorDialog;
import com.xbyg_plus.silicon.database.CachesDatabase;
import com.xbyg_plus.silicon.utils.DownloadManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    private DirectorySelectorDialog directorySelectorDialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        this.directorySelectorDialog = new DirectorySelectorDialog(getContext());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        Preference savingPath = findPreference("savingPath");
        savingPath.setSummary(preferences.getString("savingPath", Environment.getExternalStorageDirectory().getAbsolutePath()));
        savingPath.setOnPreferenceClickListener((Preference preference) -> {
            directorySelectorDialog.setOnDirectorySelectedConsumer(dir -> {
                savingPath.setSummary(dir.getAbsolutePath());
                preferences.edit().putString("savingPath", dir.getAbsolutePath()).apply();
                DownloadManager.setSavePath(dir.getAbsolutePath() + "/");
            }).show();
            return true;
        });

        Preference caches = findPreference("caches");
        caches.setSummary(CachesDatabase.getCachesSize() + " kb");

        caches.setOnPreferenceClickListener((Preference preference) -> {
            CachesDatabase.clear();
            caches.setSummary("0 kb");
            return true;
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            findPreference("caches").setSummary(CachesDatabase.getCachesSize() + " kb");
        }
    }
}
