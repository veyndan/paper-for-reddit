package com.veyndan.paper.reddit.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;

import retrofit2.Response;
import rx.Observable;

public final class Tree {

    @NonNull
    public static Observable<Node<Response<Thing<Listing>>>> flattenFrom(@NonNull final Observable<Node<Response<Thing<Listing>>>> node, @IntRange(from = 0) final int depth) {
        return node
                .doOnNext(responseNode -> responseNode.setDepth(depth))
                .concatMap(responseNode -> Observable.just(responseNode)
                        .concatWith(flattenFrom(responseNode.getChildren(), depth + 1)));
    }
}
