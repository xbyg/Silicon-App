package com.xbyg_plus.silicon.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadsDatabase {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editior;

    public static void init(Context context){
        sharedPreferences = context.getSharedPreferences("download_record",Context.MODE_PRIVATE);
        editior = sharedPreferences.edit();
    }

    public static void addDownloadPath(String name,String path){
        editior.putString(name,path);
    }

    public static void removeDownloadPath(String name){
        editior.remove(name);
    }

    public static void removeAll() {
        editior.clear().apply();
    }

    public static void save(){
        editior.apply();
    }

    public static Map<String ,String> getDownloadsPath(){
        return (Map<String, String>) sharedPreferences.getAll();
    }

    public static List<File> getDownloads(){
        List<File> files = new ArrayList<>();
        for(Map.Entry<String,String> entry : getDownloadsPath().entrySet()){
            File file = new File(entry.getValue()+entry.getKey());
            if(file != null){
                files.add(file);
            }else{
               removeDownloadPath(entry.getValue());
               save();
            }
        }
        return files;
    }
}
