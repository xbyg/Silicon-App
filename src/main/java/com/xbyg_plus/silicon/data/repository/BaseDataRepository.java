package com.xbyg_plus.silicon.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.xbyg_plus.silicon.MyApplication;
import com.xbyg_plus.silicon.data.DataSource;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseDataRepository<EntrySet> implements DataSource<EntrySet> {
    protected SharedPreferences sharedPreferences;

    protected EntrySet caches;

    //all access modifier of subclass's constructor is private for singleton pattern
    protected BaseDataRepository(String storeName) {
        this.sharedPreferences = MyApplication.getContext().getSharedPreferences(storeName, Context.MODE_PRIVATE);
    }

    @Override
    public final Single<EntrySet> getData(boolean fresh) {
        if (this.caches == null || fresh) {
            return Single.create((SingleEmitter<EntrySet> e) -> {
                this.caches = fetchData();
                e.onSuccess(this.caches);
            }).subscribeOn(Schedulers.io());
        }
        return Single.just(caches);
    }

    protected abstract EntrySet fetchData() throws IOException;
}
