package com.veyndan.paper.reddit.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import rx.Observable;

public abstract class Node<T> {

    @IntRange(from = 0) private int depth;
    @NonNull private Observable<Boolean> trigger = Observable.empty();
    @NonNull private Observable<T> request = Observable.empty();

    @IntRange(from = 0)
    public int getDepth() {
        return depth;
    }

    @NonNull
    public abstract Observable<Node<T>> getChildren();

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

    @NonNull
    public Observable<Node<T>> preOrderTraverse(@IntRange(from = 0) final int depth) {
        return Observable.just(this)
                .doOnNext(node -> node.depth = depth)
                .concatMap(node -> Observable.just(node)
                        .concatWith(node.getChildren()
                                .concatMap(childNode -> childNode.preOrderTraverse(depth + 1))));
    }
}
