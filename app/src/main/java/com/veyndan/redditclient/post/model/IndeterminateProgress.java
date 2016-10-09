package com.veyndan.redditclient.post.model;

import android.support.annotation.NonNull;

import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.util.Node;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class IndeterminateProgress extends Node<Response<Thing<Listing>>> {

    // Suppress default constructor for noninstantiability
    @SuppressWarnings("unused")
    private IndeterminateProgress() {
        throw new AssertionError();
    }

    private IndeterminateProgress(@NonNull final Builder builder) {
        setTrigger(builder.trigger);
        setRequest(builder.request);
    }

    @NonNull
    @Override
    public List<Node<Response<Thing<Listing>>>> getChildren() {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> asObservable() {
        return getTrigger().takeFirst(Boolean::booleanValue)
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(aBoolean -> getRequest().subscribeOn(Schedulers.io()))
                .observeOn(Schedulers.computation())
                .map(Response::body)
                .flatMap(thing -> Observable.from(thing.data.children)
                        .cast(Submission.class)
                        .map(Post::new)
                        .flatMap(Mutators.mutate())
                        .map(post -> (Node<Response<Thing<Listing>>>) post) // TODO Eww
                        .concatWith(Observable.just(new IndeterminateProgress.Builder()
                                .trigger(getTrigger())
                                .request(getRequest())
                                .build())));
    }

    public static class Builder {

        @NonNull private Observable<Boolean> trigger = Observable.empty();
        @NonNull private Observable<Response<Thing<Listing>>> request = Observable.empty();

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
        public IndeterminateProgress build() {
            return new IndeterminateProgress(this);
        }
    }
}
