package com.veyndan.paper.reddit.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import rx.Observable;

public abstract class Node<T> {

    private static final int UNKNOWN_DESCENDANT_COUNT = -1;

    @IntRange(from = 0) private int depth;
    @IntRange(from = UNKNOWN_DESCENDANT_COUNT) private int descendantCount = UNKNOWN_DESCENDANT_COUNT;
    @NonNull private Observable<Boolean> trigger = Observable.empty();
    @NonNull private Observable<T> request = Observable.empty();

    @IntRange(from = 0)
    public int getDepth() {
        return depth;
    }

    @NonNull
    public abstract Observable<Node<T>> getChildren();

    @IntRange(from = UNKNOWN_DESCENDANT_COUNT)
    public int getDescendantCount() {
        return descendantCount;
    }

    public void setDescendantCount(@IntRange(from = 0) final int descendantCount) {
        this.descendantCount = descendantCount;
    }

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
                .doOnNext(node -> {
                    if (node.descendantCount == UNKNOWN_DESCENDANT_COUNT) {
                        node.generateDescendantCount().subscribe(integer -> node.descendantCount = integer);
                    }
                })
                .concatMap(node -> Observable.just(node)
                        .concatWith(node.getChildren()
                                .concatMap(childNode -> childNode.preOrderTraverse(depth + 1))));
    }

    private Observable<Integer> generateDescendantCount() {
        return getChildren()
                .toList()
                .flatMap(nodes -> Observable.from(nodes)
                        .flatMap(Node::generateDescendantCount)
                        .concatWith(Observable.just(nodes.size()))
                        .scan((sum, item) -> sum + item))
                .last();
    }
}
