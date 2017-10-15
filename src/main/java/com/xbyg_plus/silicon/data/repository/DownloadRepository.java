package com.xbyg_plus.silicon.data.repository;

import android.content.SharedPreferences;

import com.xbyg_plus.silicon.data.DataModifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class DownloadRepository extends BaseDataRepository<List<File>> implements DataModifier<List<File>, File> {
    public static final String STORE_NAME = "downloads";
    public static final DownloadRepository instance = new DownloadRepository();

    private DownloadRepository() {
        super(STORE_NAME);
    }

    @Override
    protected List<File> fetchData() throws IOException {
        List<File> files = new ArrayList<>();
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            File file = new File(entry.getValue().toString());
            if (file.exists()) {
                files.add(file);
            }
        }
        return files;
    }

    @Override
    public Completable applyData() {
        return Completable.create(e -> {
            sharedPreferences.edit().clear().apply();
            e.onComplete();
        }).andThen(insertAll(caches));
    }

    @Override
    public Completable insertAll(List<File> files) {
        return Completable.create(e -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (File file : files) {
                editor.putString(file.getName(), file.getPath());
            }
            editor.apply();
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertSingle(File file) {
        return getData(false).flatMapCompletable(files -> { //caches maybe null if DownloadFragment is not initialized
            caches = files;
            caches.add(file);
            sharedPreferences.edit().putString(file.getName(), file.getPath()).apply();
            return Completable.complete();
        });
    }

    @Override
    public Completable deleteSingle(File file) {
        return getData(false).flatMapCompletable(files -> { //caches maybe null if DownloadFragment is not initialized
            caches = files;
            caches.remove(file);
            sharedPreferences.edit().remove(file.getName()).apply();
            return Completable.complete();
        });
    }

    @Override
    public void deleteAll() {
        sharedPreferences.edit().clear().apply();
    }
}
