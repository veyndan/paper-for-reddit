package com.veyndan.paper.reddit.post.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import retrofit2.Response;

public final class Progress extends Node<Response<Thing<Listing>>> {

    @Nullable @IntRange(from = 0) private final Integer degree;

    @SuppressWarnings("unused")
    private Progress() {
        throw new AssertionError("No instances.");
    }

    private Progress(@NonNull final Builder builder) {
        degree = builder.degree;
        setRequest(builder.request);
    }

    @Nullable
    @IntRange(from = 0)
    @Override
    public Integer getDegree() {
        return degree;
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> getChildren() {
        return Observable.empty();
    }

    public static class Builder {

        @Nullable @IntRange(from = 0) private Integer degree;
        @NonNull private Maybe<Response<Thing<Listing>>> request = Maybe.empty();

        @NonNull
        public Builder degree(@IntRange(from = 0) final int degree) {
            this.degree = degree;
            return this;
        }

        @NonNull
        public Builder request(@NonNull final Maybe<Response<Thing<Listing>>> request) {
            this.request = request;
            return this;
        }

        @NonNull
        public Progress build() {
            return new Progress(this);
        }
    }
}
