package com.xbyg_plus.silicon.task;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

public abstract class ObservableTask<Parameter, Result> {
    protected PublishSubject<Integer> progressPublisher = PublishSubject.create();

    public abstract Single<Result> execute(Parameter parameter);

    public Observable<Integer> progressObservable() {
        return progressPublisher;
    }
}
