package com.veyndan.paper.reddit.post.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.More;
import com.veyndan.paper.reddit.api.reddit.model.Submission;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.post.media.mutator.Mutators;
import com.veyndan.paper.reddit.util.Node;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class Progress extends Node<Response<Thing<Listing>>> {

    private static final int UNKNOWN_CHILD_COUNT = -1;

    @IntRange(from = UNKNOWN_CHILD_COUNT) private final int childCount;

    // Suppress default constructor for noninstantiability
    @SuppressWarnings("unused")
    private Progress() {
        throw new AssertionError();
    }

    private Progress(@NonNull final Builder builder) {
        childCount = builder.childCount;
        setTrigger(builder.trigger);
        setRequest(builder.request);
    }

    @IntRange(from = 0)
    public int getChildCount() {
        if (!isChildCountAvailable()) {
            throw new IllegalStateException("Check that Stub.isChildCountAvailable() before " +
                    "attempting to retrieve the child count");
        }
        return childCount;
    }

    public boolean isChildCountAvailable() {
        return childCount != UNKNOWN_CHILD_COUNT;
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> getChildren() {
        return Observable.empty();
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> asObservable() {
        return getTrigger().takeFirst(Boolean::booleanValue)
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(aBoolean -> getRequest().subscribeOn(Schedulers.io()))
                .map(Response::body)
                .flatMap(thing -> Observable.from(thing.data.children)
                        .observeOn(Schedulers.computation())
                        .concatMap(redditObject -> {
                            if (redditObject instanceof Submission) {
                                return Observable.just(redditObject)
                                        .cast(Submission.class)
                                        .map(Post::new)
                                        .flatMap(Mutators.mutate());
                            } else if (redditObject instanceof More) {
                                final More more = (More) redditObject;
                                return Observable.just(new Builder()
                                        .trigger(Observable.just(true))
                                        .childCount(more.count)
                                        .build());
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

        @IntRange(from = UNKNOWN_CHILD_COUNT) private int childCount = UNKNOWN_CHILD_COUNT;
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
        public Progress build() {
            return new Progress(this);
        }
    }
}
