package com.veyndan.redditclient.post.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.util.Node;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

public final class DeterminateProgress extends Node<Response<Thing<Listing>>> {

    @IntRange(from = 0) private int childCount = 0;

    // Suppress default constructor for noninstantiability
    @SuppressWarnings("unused")
    private DeterminateProgress() {
        throw new AssertionError();
    }

    private DeterminateProgress(@NonNull final Builder builder) {
        childCount = builder.childCount;
        setTrigger(builder.trigger);
        setRequest(builder.request);
    }

    @IntRange(from = 0)
    public int getChildCount() {
        return childCount;
    }

    @NonNull
    @Override
    public List<Node<Response<Thing<Listing>>>> getChildren() {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> asObservable() {
        return null;
    }

    public static class Builder {

        @IntRange(from = 0) private int childCount = 0;
        @NonNull private Observable<Boolean> trigger = Observable.empty();
        @NonNull private Observable<Response<Thing<Listing>>> request = Observable.empty();

        @NonNull
        public Builder childCount(@IntRange(from = 0) final int childCount) {
            this.childCount = childCount;
            return this;
        }

        @NonNull
        public Builder trigger(@NonNull final Observable<Boolean> trigger) {
            this.trigger = trigger;
            return this;
        }

        @NonNull
        public Builder request(@NonNull final Observable<Response<Thing<Listing>>> request) {
            this.request = request;
            return this;
        }

        @NonNull
        public DeterminateProgress build() {
            return new DeterminateProgress(this);
        }
    }
}
