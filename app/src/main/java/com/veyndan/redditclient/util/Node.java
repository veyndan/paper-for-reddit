package com.veyndan.redditclient.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

public abstract class Node<T> {

    @IntRange(from = 0) private int depth = 0;
    @NonNull private Observable<Boolean> trigger = Observable.empty();
    @NonNull private Observable<T> request = Observable.empty();

    @IntRange(from = 0)
    public int getDepth() {
        return depth;
    }

    public void setDepth(@IntRange(from = 0) final int depth) {
        this.depth = depth;
    }

    @NonNull
    public abstract List<Node<T>> getChildren();

    @NonNull
    public Observable<Boolean> getTrigger() {
        return trigger;
    }

    public void setTrigger(@NonNull final Observable<Boolean> trigger) {
        this.trigger = trigger;
    }

    @NonNull
    public Observable<T> getRequest() {
        return request;
    }

    public void setRequest(@NonNull final Observable<T> request) {
        this.request = request;
    }

    @NonNull
    public abstract Observable<Node<T>> asObservable();
}
