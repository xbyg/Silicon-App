package com.xbyg_plus.silicon.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OKHTTPClient {
    private static OKHTTPClient instance;

    private OkHttpClient client;
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

    public OKHTTPClient() {
        instance = this;
        client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).build();
    }

    //a GET request
    public static void get(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        instance.client.newCall(request).enqueue(callback);
    }

    //a POST request
    public static void post(String url, Map<String, String> datasets, Callback callback) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> data : datasets.entrySet()) {
                builder.addFormDataPart(data.getKey(), data.getValue());
            }
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", RequestBody.create(null, new byte[0]))
                    .post(requestBody)
                    .build();
            instance.client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
