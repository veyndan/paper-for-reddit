package com.veyndan.redditclient.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import rx.Observable;

public class Node<T> {

    private static final int UNKNOWN_CHILD_COUNT = -1;

    @IntRange(from = 0) private int depth;
    @NonNull private final List<Node<T>> children;
    @IntRange(from = UNKNOWN_CHILD_COUNT) private final int childCount;
    @NonNull private final Observable<Boolean> trigger;
    @NonNull private final Observable<T> request;
    private final boolean stub;

    public Node() {
        this(new Builder<>());
    }

    private Node(final Builder<T> builder) {
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
    public List<Node<T>> getChildren() {
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
    public Observable<T> getRequest() {
        return request;
    }

    public boolean isStub() {
        return stub;
    }

    public static class Builder<T> {

        @IntRange(from = 0) private int depth = 0;
        @NonNull private List<Node<T>> children = Collections.emptyList();
        @IntRange(from = UNKNOWN_CHILD_COUNT) private int childCount = UNKNOWN_CHILD_COUNT;
        @NonNull private Observable<Boolean> trigger = Observable.empty();
        @NonNull private Observable<T> request = Observable.empty();
        private boolean stub = false;

        @NonNull
        public Builder<T> depth(@IntRange(from = 0) final int depth) {
            this.depth = depth;
            return this;
        }

        @NonNull
        public Builder<T> children(final List<Node<T>> children) {
            this.children = children;
            if (childCount == UNKNOWN_CHILD_COUNT) {
                childCount = children.size();
            }
            return this;
        }

        @NonNull
        public Builder<T> childCount(@IntRange(from = 0) final int childCount) {
            this.childCount = childCount;
            return this;
        }

        @NonNull
        public Builder<T> trigger(@NonNull final Observable<Boolean> trigger) {
            this.trigger = trigger;
            return this;
        }

        @NonNull
        public Builder<T> request(@NonNull final Observable<T> request) {
            this.request = request;
            return this;
        }

        @NonNull
        public Builder<T> stub(final boolean stub) {
            this.stub = stub;
            return this;
        }

        @NonNull
        public Node<T> build() {
            return new Node<>(this);
        }
    }
}
