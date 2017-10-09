package com.xbyg_plus.silicon.data.repository;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface DataRepository<EntrySet, Entry> {
    Single<EntrySet> get(boolean fresh);

    Completable insertSingle(Entry entry);

    Completable insertAll(EntrySet entrySet);

    Completable deleteSingle(Entry entry);

    void deleteAll();

    void save();
}
