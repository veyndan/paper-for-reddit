package com.veyndan.paper.reddit.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;

interface INode<T> {

    @IntRange(from = 0)
    int depth();

    /**
     * Returns the degree of this node, or else {@code null} if the degree is unknown.
     */
    @Nullable
    @IntRange(from = 0)
    Integer degree();

    @NonNull
    Observable<Node<T>> children();

    /**
     * Returns the descendant count of this node, or else {@code null} if the descendant count is
     * unknown.
     */
    @Nullable
    @IntRange(from = 0)
    Integer descendantCount();

    @NonNull
    Observable<Node<T>> preOrderTraverse();

    default boolean internalNode() {
        return descendantCount() > 0;
    }
}
