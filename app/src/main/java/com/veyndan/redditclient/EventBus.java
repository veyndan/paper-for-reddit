package com.veyndan.redditclient;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public final class EventBus {

    private static final EventBus INSTANCE = new EventBus();

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    private EventBus() {
    }

    public static EventBus getInstance() {
        return INSTANCE;
    }

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
