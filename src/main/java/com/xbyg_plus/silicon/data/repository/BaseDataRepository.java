package com.xbyg_plus.silicon.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.xbyg_plus.silicon.MyApplication;

import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseDataRepository<EntrySet, Entry> implements DataRepository<EntrySet, Entry> {
    protected SharedPreferences sharedPreferences;

    protected EntrySet caches;

    //all access modifier of subclass's constructor is private for singleton pattern
    protected BaseDataRepository(String storeName) {
        this.sharedPreferences = MyApplication.getContext().getSharedPreferences(storeName, Context.MODE_PRIVATE);
    }

    @Override
    public final Single<EntrySet> get(boolean fresh) {
        if (this.caches == null || fresh) {
            return Single.create((SingleEmitter<EntrySet> e) -> {
                this.caches = fetchData();
                e.onSuccess(this.caches);
            }).subscribeOn(Schedulers.io());
        }
        return Single.just(caches);
    }

    protected abstract EntrySet fetchData() throws IOException;

    //wraps the 'writeSingle' method and invokes it under io thread
    @Override
    public final Completable insertSingle(Entry entry) {
        return Completable.create(e -> {
            writeSingle(entry);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    protected abstract void writeSingle(Entry entry) throws IOException;

    //wraps the 'writeAll' method and invokes it under io thread
    @Override
    public final Completable insertAll(EntrySet entrySet) {
        return Completable.create(e -> {
            writeAll(entrySet);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    protected abstract void writeAll(EntrySet entrySet) throws IOException;

    //wraps the 'wipeSingle' method and invokes it under io thread
    @Override
    public Completable deleteSingle(Entry entry) {
        return Completable.create(e -> {
            wipeSingle(entry);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    protected abstract void wipeSingle(Entry entry) throws IOException;

    @Override
    public final void deleteAll() {
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public final void save() {
        Completable.create(e -> {
            if (this.caches != null) {
                sharedPreferences.edit().clear().apply();
                writeAll(this.caches);
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
