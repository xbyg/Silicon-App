package com.xbyg_plus.silicon.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.data.repository.NoticeRepository;
import com.xbyg_plus.silicon.data.repository.PastPaperRepository;
import com.xbyg_plus.silicon.dialog.DirectorySelectorDialog;

import io.reactivex.Maybe;
import io.reactivex.Observable;

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
            }).show();
            return true;
        });

        Preference caches = findPreference("caches");

        getDataSize(NoticeRepository.STORE_NAME, PastPaperRepository.STORE_NAME)
                .subscribe(size -> caches.setSummary((size / 1024) + " kb"));

        caches.setOnPreferenceClickListener((Preference preference) -> {
            getContext().getSharedPreferences(NoticeRepository.STORE_NAME, Context.MODE_PRIVATE).edit().clear().apply();
            getContext().getSharedPreferences(PastPaperRepository.STORE_NAME, Context.MODE_PRIVATE).edit().clear().apply();
            caches.setSummary("0 kb");
            return true;
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getDataSize(NoticeRepository.STORE_NAME, PastPaperRepository.STORE_NAME)
                    .subscribe(size -> findPreference("caches").setSummary((size / 1024) + " kb"));
        }
    }

    private Maybe<Integer> getDataSize(String... storesName) {
        return Observable.fromArray(storesName)
                .flatMap(storeName -> Observable.fromIterable(getContext().getSharedPreferences(storeName, Context.MODE_PRIVATE).getAll().values()))
                .map(o -> o.toString().length())
                .reduce((count, len) -> count += len);
    }
}
