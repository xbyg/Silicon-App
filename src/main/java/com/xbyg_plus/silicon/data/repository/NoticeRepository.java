package com.xbyg_plus.silicon.data.repository;

import android.content.SharedPreferences;

import com.xbyg_plus.silicon.data.factory.NoticeInfoFactory;
import com.xbyg_plus.silicon.model.WebNoticeInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoticeRepository extends ORMRepository<List<WebNoticeInfo>, WebNoticeInfo, NoticeInfoFactory> {
    public static final String STORE_NAME = "notice";
    public static final NoticeRepository instance = new NoticeRepository();

    private NoticeRepository() {
        super(STORE_NAME, new NoticeInfoFactory());
    }

    @Override
    protected List<WebNoticeInfo> fetchData() throws IOException {
        List<WebNoticeInfo> list = new ArrayList<>();
        for (Object o : sharedPreferences.getAll().values()) {
            list.add(entryFactory.deserialize(o.toString(), mapper));
        }
        return list;
    }

    @Override
    protected void writeAll(List<WebNoticeInfo> noticeInfoList) throws IOException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (WebNoticeInfo noticeInfo : noticeInfoList) {
            editor.putString(noticeInfo.getName(), entryFactory.serialize(noticeInfo, mapper));
        }
        editor.apply();
    }

    @Override
    protected void writeSingle(WebNoticeInfo noticeInfo) throws IOException {
        get(false).subscribe(noticeInfoList -> {
            caches = noticeInfoList;
            caches.add(noticeInfo);
            sharedPreferences.edit().putString(noticeInfo.getName(), entryFactory.serialize(noticeInfo, mapper)).apply();
        });
    }

    @Override
    protected void wipeSingle(WebNoticeInfo noticeInfo) throws IOException {
        get(false).subscribe(noticeInfoList -> {
            caches = noticeInfoList;
            caches.add(noticeInfo);
            sharedPreferences.edit().remove(noticeInfo.getName()).apply();
        });
    }
}
