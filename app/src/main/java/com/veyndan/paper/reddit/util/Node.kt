package com.veyndan.paper.reddit.util

import android.support.annotation.IntRange
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

abstract class Node<T> {

    @IntRange(from = 0)
    var depth: Int = 0

    /**
     * Returns the degree of this node, or else {@code Maybe.empty()} if the degree is unknown.
     */
    @IntRange(from = 0)
    open fun degree(): Maybe<Long> {
        return children().count().toMaybe()
    }

    fun internalNode(): Single<Boolean> {
        return descendantCount()
                .map { it > 0 }
    }

    abstract fun children(): Observable<Node<T>>

    open fun descendantCount(): Single<Int> {
        return children()
                .flatMapSingle(Node<T>::descendantCount)
                .reduce(degree().blockingGet().toInt()) { sum, item -> sum + item }
    }

    fun preOrderTraverse(@IntRange(from = 0) depth: Int): Observable<Node<T>> {
        return Observable.just(this)
                // Node specific calculations are done here. This is the soonest that these
                // calculations can be performed, as before this point the node data came from
                // some unknown place, e.g. a network request, disk etc.
                .doOnNext { it.depth = depth }
                .concatWith(children()
                        .concatMap { it.preOrderTraverse(depth + 1) })
    }
}
