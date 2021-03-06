package com.xbyg_plus.silicon.utils;

import com.xbyg_plus.silicon.MyApplication;
import com.xbyg_plus.silicon.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class OKHTTPClient {
    private static OkHttpClient client;
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    public static void init() {
        client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                }).build();
    }

    //a GET request
    public static Single<String> get(String url) {
        return Single.create((SingleEmitter<String> e) -> {
            Response res = client.newCall(new Request.Builder().url(url).build()).execute();
            if (res.isSuccessful()) {
                e.onSuccess(res.body().string());
            } else {
                e.onError(new IOException(MyApplication.getContext().getString(R.string.io_exception)));
            }
        }).subscribeOn(Schedulers.io());
    }

    //a POST request
    public static Single<String> post(String url, Map<String, String> postData) {
        return Single.create((SingleEmitter<String> e) -> {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> data : postData.entrySet()) {
                builder.addFormDataPart(data.getKey(), data.getValue());
            }
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", RequestBody.create(null, new byte[0]))
                    .post(requestBody)
                    .build();
            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                e.onSuccess(res.body().string());
            } else {
                e.onError(new IOException(MyApplication.getContext().getString(R.string.io_exception)));
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Single<InputStream> stream(String url) {
        return Single.create((SingleEmitter<InputStream> e) -> {
            Response res = client.newCall(new Request.Builder().url(url).build()).execute();
            if (res.isSuccessful()) {
                e.onSuccess(res.body().byteStream());
            } else {
                e.onError(new IOException(MyApplication.getContext().getString(R.string.io_exception)));
            }
        }).subscribeOn(Schedulers.io());
    }

    public static HashMap<String, List<Cookie>> getCookieStore() {
        return cookieStore;
    }
}
