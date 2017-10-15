package com.xbyg_plus.silicon.data.repository;

import android.content.SharedPreferences;

import com.xbyg_plus.silicon.data.DataModifier;
import com.xbyg_plus.silicon.data.factory.NotificationFactory;
import com.xbyg_plus.silicon.model.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class NotificationRepository extends ORMRepository<List<Notification>, Notification, NotificationFactory> implements DataModifier<List<Notification>, Notification> {
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
        Collections.sort(list, ((n1, n2) -> n1.getDate() < n2.getDate() ? -1 : 1));
        return list;
    }

    @Override
    public Completable applyData() {
        return Completable.create(e -> {
            sharedPreferences.edit().clear().apply();
            e.onComplete();
        }).andThen(insertAll(caches));
    }

    @Override
    public Completable insertAll(List<Notification> notificationList) {
        return Completable.create(e -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (Notification notification : notificationList) {
                editor.putString(notification.getDate().toString(), entryFactory.serialize(notification, mapper));
            }
            editor.apply();
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertSingle(Notification notification) {
        return getData(false).flatMapCompletable(notificationList -> { //caches maybe null if NotificationFragment is not initialized
            caches = notificationList;
            caches.add(notification);
            sharedPreferences.edit().putString(notification.getDate().toString(), entryFactory.serialize(notification, mapper)).apply();
            return Completable.complete();
        });
    }

    @Override
    public Completable deleteSingle(Notification notification) {
        return getData(false).flatMapCompletable(notificationList -> { //caches maybe null if NotificationFragment is not initialized
            caches = notificationList;
            caches.remove(notification);
            sharedPreferences.edit().remove(notification.getDate().toString()).apply();
            return Completable.complete();
        });
    }

    @Override
    public void deleteAll() {
        sharedPreferences.edit().clear().apply();
    }
}
