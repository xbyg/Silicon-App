package com.xbyg_plus.silicon.data.repository;

import android.content.SharedPreferences;

import com.xbyg_plus.silicon.data.factory.PastPaperFactory;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastPaperRepository extends ORMRepository<Map<String, List<WebResourceInfo>>, Map.Entry<String, List<WebResourceInfo>>, PastPaperFactory> {
    public static final String STORE_NAME = "past paper";
    public static final PastPaperRepository instance = new PastPaperRepository();

    private PastPaperRepository() {
        super(STORE_NAME, new PastPaperFactory());
    }

    @Override
    protected Map<String, List<WebResourceInfo>> fetchData() throws IOException {
        HashMap<String, List<WebResourceInfo>> map = new HashMap<>();
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            Map.Entry<String, List<WebResourceInfo>> folderContents = entryFactory.deserialize(entry.getValue().toString(), mapper);
            map.put(folderContents.getKey(), folderContents.getValue());
        }
        return map;
    }

    @Override
    public void writeAll(Map<String, List<WebResourceInfo>> contentsIndex) throws IOException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, List<WebResourceInfo>> entry : contentsIndex.entrySet()) {
            editor.putString(entry.getKey(), entryFactory.serialize(entry, mapper));
        }
        editor.apply();
    }

    @Override
    public void writeSingle(Map.Entry<String, List<WebResourceInfo>> entry) throws IOException {
        get(false).subscribe(contentsIndex -> {
            caches = contentsIndex;
            caches.put(entry.getKey(), entry.getValue());
            sharedPreferences.edit().putString(entry.getKey(), entryFactory.serialize(entry, mapper)).apply();
        });
    }

    @Override
    protected void wipeSingle(Map.Entry<String, List<WebResourceInfo>> entry) throws IOException {
        get(false).subscribe(contentsIndex -> {
            caches = contentsIndex;
            caches.remove(entry.getKey());
            sharedPreferences.edit().remove(entry.getKey()).apply();
        });
    }
}
