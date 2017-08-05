package com.xbyg_plus.silicon.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xbyg_plus.silicon.model.WebNoticeInfo;
import com.xbyg_plus.silicon.model.WebPastPaperFolderInfo;
import com.xbyg_plus.silicon.model.WebPastPaperInfo;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CachesDatabase {
    private static Gson gson;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static HashMap<String, List<WebResourceInfo>> contentsIndex;
    private static List<WebNoticeInfo> noticeList;

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("caches", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        RuntimeTypeAdapterFactory<WebResourceInfo> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(WebResourceInfo.class)
                .registerSubtype(WebPastPaperInfo.class)
                .registerSubtype(WebPastPaperFolderInfo.class)
                .registerSubtype(WebNoticeInfo.class);
        gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();

        Type type1 = new TypeToken<HashMap<String, List<WebResourceInfo>>>() {}.getType();
        contentsIndex = sharedPreferences.contains("contentsIndex") ? gson.fromJson(sharedPreferences.getString("contentsIndex", ""), type1) : new HashMap<>();

        Type type2 = new TypeToken<List<WebNoticeInfo>>() {}.getType();
        noticeList = sharedPreferences.contains("noticeList") ? gson.fromJson(sharedPreferences.getString("noticeList", ""), type2) : new ArrayList<>();
    }

    public static HashMap<String, List<WebResourceInfo>> getContentsIndex() {
        return contentsIndex;
    }

    public static List<WebNoticeInfo> getNoticeList() {
        return noticeList;
    }

    public static void save() {
        editor.putString("contentsIndex", gson.toJson(contentsIndex));
        editor.putString("noticeList", gson.toJson(noticeList));
        editor.apply();
    }

    public static void clear() {
        contentsIndex.clear();
        noticeList.clear();
        save();
    }

    public static int getCachesSize() { //in kb
        return (sharedPreferences.getString("contentsIndex", "").getBytes().length + sharedPreferences.getString("noticeList", "").getBytes().length) / 1000;
    }
}
