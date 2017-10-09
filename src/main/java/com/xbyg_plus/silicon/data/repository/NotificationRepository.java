package com.xbyg_plus.silicon.data.repository;

import android.content.SharedPreferences;

import com.xbyg_plus.silicon.data.factory.NotificationFactory;
import com.xbyg_plus.silicon.model.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository extends ORMRepository<List<Notification>, Notification, NotificationFactory> {
    public static final String STORE_NAME = "notification";
    public static final NotificationRepository instance = new NotificationRepository();

    private NotificationRepository() {
        super(STORE_NAME, new NotificationFactory());
    }

    @Override
    protected List<Notification> fetchData() throws IOException {
        List<Notification> list = new ArrayList<>();
        for (Object o : sharedPreferences.getAll().values()) {
            list.add(entryFactory.deserialize(o.toString(), mapper));
        }
        return list;
    }

    @Override
    public void writeAll(List<Notification> notificationList) throws IOException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Notification notification : notificationList) {
            editor.putString(notification.getTitle(), entryFactory.serialize(notification, mapper));
        }
        editor.apply();
    }

    @Override
    public void writeSingle(Notification notification) throws IOException {
        get(false).subscribe(notificationList -> { //caches maybe null if NotificationFragment is not initialized
            caches = notificationList;
            caches.add(notification);
            sharedPreferences.edit().putString(notification.getTitle(), entryFactory.serialize(notification, mapper)).apply();
        });
    }

    @Override
    protected void wipeSingle(Notification notification) throws IOException {
        get(false).subscribe(notificationList -> { //caches maybe null if NotificationFragment is not initialized
            caches = notificationList;
            caches.remove(notification);
            sharedPreferences.edit().remove(notification.getTitle()).apply();
        });
    }
}
