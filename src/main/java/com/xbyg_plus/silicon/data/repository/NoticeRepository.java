package com.xbyg_plus.silicon.data.repository;

import android.content.SharedPreferences;

import com.xbyg_plus.silicon.data.factory.NoticeInfoFactory;
import com.xbyg_plus.silicon.model.WebNoticeInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;

public class NoticeRepository extends ORMRepository<List<WebNoticeInfo>, WebNoticeInfo, NoticeInfoFactory> {
    public static final String STORE_NAME = "notice";
    public static final NoticeRepository instance = new NoticeRepository();

    private NoticeRepository() {
        super(STORE_NAME, new NoticeInfoFactory());
    }

    @Override
    protected List<WebNoticeInfo> fetchData() throws IOException {
        Map<String, ?> map = sharedPreferences.getAll();
        int size = map.size();

        WebNoticeInfo[] noticesInfo = new WebNoticeInfo[size];
        for (Integer i = 0; i < size; i++) {
            noticesInfo[i] = entryFactory.deserialize(map.get(i.toString()).toString(), mapper);
        }
        return new ArrayList<>(Arrays.asList(noticesInfo));
    }

    @Override
    public Completable applyData() {
        return Completable.create(e -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();

            for (Integer i = 0; i < caches.size(); i++) {
                editor.putString(i.toString(), entryFactory.serialize(caches.get(i), mapper));
            }
            editor.apply();
            e.onComplete();
        });
    }
}
