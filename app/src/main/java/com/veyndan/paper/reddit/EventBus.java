package com.veyndan.paper.reddit;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public enum EventBus {

    INSTANCE;

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void send(final Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}
