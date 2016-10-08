package com.veyndan.redditclient.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class Node {

    private static final int UNKNOWN_CHILD_COUNT = -1;

    @IntRange(from = 0) private int depth;
    @NonNull private final List<Node> children;
    @IntRange(from = UNKNOWN_CHILD_COUNT) private final int childCount;
    @NonNull private final Observable<Boolean> trigger;
    @NonNull private final Observable<Response<Thing<Listing>>> request;
    private final boolean stub;

    public Node() {
        this(new Builder());
    }

    private Node(final Builder builder) {
        depth = builder.depth;
        children = builder.children;
        childCount = builder.childCount;
        trigger = builder.trigger;
        request = builder.request;
        stub = builder.stub;
    }

    @IntRange(from = 0)
    public int getDepth() {
        return depth;
    }

    public void setDepth(@IntRange(from = 0) final int depth) {
        this.depth = depth;
    }

    @NonNull
    public List<Node> getChildren() {
        return children;
    }

    @IntRange(from = 0)
    public int getChildCount() {
        if (!isChildCountAvailable()) throw new IllegalStateException("Check that " +
                "Stub.isChildCountAvailable() before attempting to retrieve the child count");
        return childCount;
    }

    public boolean isChildCountAvailable() {
        return childCount != UNKNOWN_CHILD_COUNT;
    }

    @NonNull
    public Observable<Boolean> getTrigger() {
        return trigger;
    }

    @NonNull
    public Observable<Response<Thing<Listing>>> getRequest() {
        return request;
    }

    public boolean isStub() {
        return stub;
    }

    public static class Builder {

        @IntRange(from = 0) private int depth = 0;
        @NonNull private List<Node> children = Collections.emptyList();
        @IntRange(from = UNKNOWN_CHILD_COUNT) private int childCount = UNKNOWN_CHILD_COUNT;
        @NonNull private Observable<Boolean> trigger = Observable.empty();
        @NonNull private Observable<Response<Thing<Listing>>> request = Observable.empty();
        private boolean stub = false;

        @NonNull
        public Builder depth(@IntRange(from = 0) final int depth) {
            this.depth = depth;
            return this;
        }

        @NonNull
        public Builder children(final List<Node> children) {
            this.children = children;
            if (childCount == UNKNOWN_CHILD_COUNT) {
                childCount = children.size();
            }
            return this;
        }

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
        public Builder stub(final boolean stub) {
            this.stub = stub;
            return this;
        }

        @NonNull
        public Node build() {
            return new Node(this);
        }
    }
}
