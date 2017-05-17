package com.veyndan.paper.reddit.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.Single;

public abstract class Node<T> {

    @IntRange(from = 0) private int depth;

    @IntRange(from = 0)
    public int depth() {
        return depth;
    }

    /**
     * Returns the degree of this node, or else {@code null} if the degree is unknown.
     */
    @Nullable
    @IntRange(from = 0)
    public Integer degree() {
        return (int) (long) children().count().blockingGet();
    }

    @NonNull
    public Single<Boolean> internalNode() {
        return descendantCount()
                .map(descendantCount -> descendantCount > 0);
    }

    @NonNull
    public abstract Observable<Node<T>> children();

    @NonNull
    public Single<Integer> descendantCount() {
        return children()
                .flatMapSingle(Node::descendantCount)
                .reduce(degree(), (sum, item) -> sum + item);
    }

    @NonNull
    public Observable<Node<T>> preOrderTraverse(@IntRange(from = 0) final int depth) {
        return Observable.just(this)
                // Node specific calculations are done here. This is the soonest that these
                // calculations can be performed, as before this point the node data came from
                // some unknown place, e.g. a network request, disk etc.
                .doOnNext(node -> node.depth = depth)
                .concatWith(children()
                        .concatMap(childNode -> childNode.preOrderTraverse(depth + 1)));
    }
}
