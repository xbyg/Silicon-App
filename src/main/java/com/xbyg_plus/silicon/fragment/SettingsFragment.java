package com.xbyg_plus.silicon.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.DirectorySelectorDialog;
import com.xbyg_plus.silicon.utils.CachesDatabase;

import java.io.File;

public class SettingsFragment extends PreferenceFragmentCompat{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        Preference savingPath = findPreference("savingPath");
        Preference caches = findPreference("caches");

        savingPath.setSummary(preferences.getString("savingPath","/sdcard/"));
        caches.setSummary(CachesDatabase.getCachesSize()+" kb");

        savingPath.setOnPreferenceClickListener( (Preference preference) -> {
            new DirectorySelectorDialog(getContext(), (dir) -> {
                savingPath.setSummary(dir);
                preferences.edit().putString("savingPath",dir).apply();
            }).show(new File("/sdcard/"));
            return true;
        });
        caches.setOnPreferenceClickListener( (Preference preference) -> {
            CachesDatabase.pastPaperFolders.clear();
            CachesDatabase.noticeList.clear();
            CachesDatabase.save();
            caches.setSummary("0 kb");
            return true;
        });
    }
}
