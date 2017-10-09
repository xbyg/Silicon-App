package com.xbyg_plus.silicon.data.repository;

import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadRepository extends BaseDataRepository<List<File>, File> {
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
    protected void writeAll(List<File> files) throws IOException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (File file : files) {
            editor.putString(file.getName(), file.getPath());
        }
        editor.apply();
    }

    @Override
    protected void writeSingle(File file) throws IOException {
        get(false).subscribe(files -> { //caches maybe null if DownloadFragment is not initialized
            caches = files;
            caches.add(file);
            sharedPreferences.edit().putString(file.getName(), file.getPath()).apply();
        });
    }

    @Override
    protected void wipeSingle(File file) throws IOException {
        get(false).subscribe(files -> { //caches maybe null if DownloadFragment is not initialized
            caches = files;
            caches.remove(file);
            sharedPreferences.edit().remove(file.getName()).apply();
        });
    }
}
