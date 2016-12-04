package com.veyndan.paper.reddit.post.model;

import android.support.annotation.NonNull;

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

    @NonNull private final Observable<Boolean> trigger;
    @NonNull private final Maybe<Integer> degree;

    // Suppress default constructor for noninstantiability
    @SuppressWarnings("unused")
    private Progress() {
        throw new AssertionError();
    }

    private Progress(@NonNull final Builder builder) {
        degree = builder.degree;
        trigger = builder.trigger;
        setRequest(builder.request);
    }

    @NonNull
    @Override
    public Maybe<Integer> getDegree() {
        return degree;
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> getChildren() {
        return Observable.empty();
    }

    @NonNull
    @Override
    public Observable<Boolean> getTrigger() {
        return trigger;
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> asObservableImpl() {
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
                                        .trigger(Observable.just(true))
                                        .degree(more.count)
                                        .build())
                                        .toObservable();
                            } else {
                                throw new IllegalStateException("Unknown node class: " + redditObject);
                            }
                        })
                        .concatWith(Observable.just(new Builder()
                                .trigger(getTrigger())
                                .request(getRequest())
                                .build())));
    }

    public static class Builder {

        @NonNull private Maybe<Integer> degree = Maybe.empty();
        @NonNull private Observable<Boolean> trigger = Observable.empty();
        @NonNull private Maybe<Response<Thing<Listing>>> request = Maybe.empty();

        @NonNull
        public Builder degree(final int degree) {
            this.degree = Maybe.just(degree);
            return this;
        }

        @NonNull
        public Builder trigger(@NonNull final Observable<Boolean> trigger) {
            this.trigger = trigger;
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
