package com.xbyg_plus.silicon.data;

import io.reactivex.Completable;

/**
 * The implementation of this interface provides the utility of altering the data directly without retrieving data from DataSource first
 * */
public interface DataModifier<EntrySet, Entry> {
    Completable insertSingle(Entry entry);

    Completable insertAll(EntrySet entrySet);

    Completable deleteSingle(Entry entry);

    void deleteAll();
}