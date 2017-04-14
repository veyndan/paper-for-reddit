package com.veyndan.paper.reddit.post.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.More;
import com.veyndan.paper.reddit.api.reddit.model.Submission;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.post.media.mutator.Mutators;
import com.veyndan.paper.reddit.util.Node;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public final class Progress extends Node<Response<Thing<Listing>>> {

    @NonNull private final Observable<Boolean> events;
    @Nullable @IntRange(from = 0) private final Integer degree;

    @SuppressWarnings("unused")
    private Progress() {
        throw new AssertionError("No instances.");
    }

    private Progress(@NonNull final Builder builder) {
        degree = builder.degree;
        events = builder.events;
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

    @NonNull
    @Override
    public Observable<Boolean> getEvents() {
        return events;
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> asObservable() {
        return getRequest()
                .subscribeOn(Schedulers.io())
                .map(Response::body)
                .toObservable()
                .flatMap(thing -> Observable.fromIterable(thing.data.children)
                        .observeOn(Schedulers.computation())
                        .concatMap(redditObject -> {
                            if (redditObject instanceof Submission) {
                                return Single.just(redditObject)
                                        .cast(Submission.class)
                                        .map(Post::new)
                                        .flatMap(Mutators.mutate())
                                        .toObservable();
                            } else if (redditObject instanceof More) {
                                final More more = (More) redditObject;
                                return Single.just(new Builder()
                                        .events(Observable.just(true))
                                        .degree(more.count)
                                        .build())
                                        .toObservable();
                            } else {
                                throw new IllegalStateException("Unknown node class: " + redditObject);
                            }
                        })
                        .concatWith(Observable.just(new Builder()
                                .events(getEvents())
                                .request(getRequest())
                                .build())));
    }

    public static class Builder {

        @Nullable @IntRange(from = 0) private Integer degree;
        @NonNull private Observable<Boolean> events = Observable.empty();
        @NonNull private Maybe<Response<Thing<Listing>>> request = Maybe.empty();

        @NonNull
        public Builder degree(@IntRange(from = 0) final int degree) {
            this.degree = degree;
            return this;
        }

        @NonNull
        public Builder events(@NonNull final Observable<Boolean> events) {
            this.events = events;
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
