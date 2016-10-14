package com.veyndan.redditclient.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;

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
