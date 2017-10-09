package com.xbyg_plus.silicon.task;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public abstract class ObservableTask<Parameter, Result> {
    protected PublishSubject<Integer> progressPublisher = PublishSubject.create();
    protected PublishSubject<Result> resultObservable = PublishSubject.create();

    public abstract void execute(Parameter parameter);

    public Observable<Integer> getProgressObservable() {
        return progressPublisher;
    }

    public Observable<Result> getResultObservable() {
        return resultObservable;
    }
}
