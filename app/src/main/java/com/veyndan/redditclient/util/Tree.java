package com.veyndan.redditclient.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;

import retrofit2.Response;
import rx.Observable;

public final class Tree {

    public static final int MAX_DEPTH_INFINITE = -1;

    @NonNull
    public static Observable<Node<Response<Thing<Listing>>>> flattenFrom(@NonNull final Observable<Node<Response<Thing<Listing>>>> node,
                                                                         @IntRange(from = 0) final int depth,
                                                                         @IntRange(from = MAX_DEPTH_INFINITE) final int maxDepth) {
        return node
                .filter(node1 -> maxDepth == MAX_DEPTH_INFINITE || depth != maxDepth)
                .doOnNext(responseNode -> responseNode.setDepth(depth))
                .concatMap(responseNode -> Observable.just(responseNode)
                        .concatWith(flattenFrom(responseNode.getChildren(), depth + 1, maxDepth)));
    }
}
