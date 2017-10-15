package com.xbyg_plus.silicon.data;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * The implementation of this interface provides the utility of retrieving data and allow altering it and apply the changes
 * */
public interface DataSource<EntrySet> {
    Single<EntrySet> getData(boolean fresh);

    Completable applyData();
}
