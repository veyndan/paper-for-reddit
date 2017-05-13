package com.veyndan.paper.reddit.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Maybe;
import io.reactivex.Observable;

// TODO NOTE:
// This is a taste of what pure logical layers (math, physics, programming) looks like. The below
// should work, but is so inefficient that the physics and programming logical levels need to
// optimize it as before through overriding this implementation.
public abstract class Node<T> {

    public Maybe<Integer> depth() {
        return parent()
                .flatMap(Node::depth)
                .map(depth -> depth + 1);
    }

    /**
     * Returns the degree of this node, or else {@code null} if the degree is unknown.
     */
    @Nullable
    @IntRange(from = 0)
    public abstract Integer degree();

    public boolean internalNode() {
        return descendantCount() > 0;
    }

    @NonNull
    public abstract Maybe<Node<T>> parent();

    @NonNull
    public abstract Observable<Node<T>> children();

    /**
     * Returns the descendant count of this node.
     */
    @IntRange(from = 0)
    public int descendantCount() {
        return 1 + children()
                .map(Node::descendantCount)
                .reduce((sum, item) -> sum + item)
                .blockingGet();
    }

    @NonNull
    public Observable<Node<T>> preOrderTraverse() {
        return Observable.just(this)
                .concatMap(node -> Observable.just(node)
                        .concatWith(node.children()
                                .concatMap(Node::preOrderTraverse)));
    }
}
