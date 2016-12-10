package com.veyndan.paper.reddit.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Maybe;
import io.reactivex.Observable;

public abstract class Node<T> {

    @IntRange(from = 0) private int depth;
    @Nullable @IntRange(from = 0) private Integer descendantCount;
    @NonNull private Maybe<T> request = Maybe.empty();

    @IntRange(from = 0)
    public int getDepth() {
        return depth;
    }

    /**
     * Returns the degree of this node, or else {@code null} if the degree is unknown.
     */
    @Nullable
    @IntRange(from = 0)
    public abstract Integer getDegree();

    @NonNull
    public abstract Observable<Node<T>> getChildren();

    /**
     * Returns the descendant count of this node, or else {@code null} if the descendant count is
     * unknown.
     */
    @Nullable
    @IntRange(from = 0)
    public Integer getDescendantCount() {
        return descendantCount;
    }

    public void setDescendantCount(@Nullable @IntRange(from = 0) final Integer descendantCount) {
        this.descendantCount = descendantCount;
    }

    @NonNull
    public abstract Observable<Boolean> getTrigger();

    @NonNull
    public Maybe<T> getRequest() {
        return request;
    }

    public void setRequest(@NonNull final Maybe<T> request) {
        this.request = request;
    }

    @NonNull
    public abstract Observable<Node<T>> asObservableImpl();

    @NonNull
    public Observable<Node<T>> asObservable() {
        return asObservableImpl()
                // Node specific calculations are done here. This is the soonest that these
                // calculations can be performed, as before this point the node data came from
                // some unknown place, e.g. a network request, disk etc.
                .doOnNext(node -> node.depth = depth)
                .doOnNext(node -> {
                    if (node.descendantCount == null) {
                        node.generateDescendantCount().subscribe(integer -> node.descendantCount = integer);
                    }
                });
    }

    @NonNull
    public Observable<Node<T>> preOrderTraverse(@IntRange(from = 0) final int depth) {
        return Observable.just(this)
                .doOnNext(node -> node.depth = depth)
                .doOnNext(node -> {
                    if (node.descendantCount == null) {
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
                .toObservable()
                .flatMap(nodes -> Observable.fromIterable(nodes)
                        .flatMap(Node::generateDescendantCount)
                        .concatWith(Observable.just(nodes.size()))
                        .scan((sum, item) -> sum + item))
                .lastElement()
                .toObservable();
    }
}
