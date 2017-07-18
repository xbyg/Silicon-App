package com.xbyg_plus.silicon.utils;

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


public class CachesDatabase {
    private static Gson gson;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editior;

    public static HashMap<String,List<WebResourceInfo>> pastPaperFolders;
    public static List<WebNoticeInfo> noticeList;

    public static void init(Context context){
        sharedPreferences = context.getSharedPreferences("caches",Context.MODE_PRIVATE);
        editior = sharedPreferences.edit();

        RuntimeTypeAdapterFactory<WebResourceInfo> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(WebResourceInfo.class)
                .registerSubtype(WebPastPaperInfo.class)
                .registerSubtype(WebPastPaperFolderInfo.class)
                .registerSubtype(WebNoticeInfo.class);
        gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();

        Type type1 = new TypeToken<HashMap<String,List<WebResourceInfo>>>(){}.getType();
        pastPaperFolders = sharedPreferences.contains("pastPaperFolders") ? gson.fromJson(sharedPreferences.getString("pastPaperFolders",""),type1) : new HashMap<>();

        Type type2 = new TypeToken<List<WebNoticeInfo>>(){}.getType();
        noticeList = sharedPreferences.contains("noticeList") ? gson.fromJson(sharedPreferences.getString("noticeList",""),type2) : new ArrayList<>();
    }

    public static void removeAll() {
        editior.clear().apply();
    }

    public static void save(){
        editior.putString("pastPaperFolders",gson.toJson(pastPaperFolders));
        editior.putString("noticeList",gson.toJson(noticeList));
        editior.apply();
    }

    public static int getCachesSize(){ //in kb
        return  (sharedPreferences.getString("pastPaperFolders","").getBytes().length + sharedPreferences.getString("noticeList","").getBytes().length)/1000;
    }
}
