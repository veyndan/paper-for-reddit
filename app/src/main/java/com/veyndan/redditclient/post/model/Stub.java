package com.veyndan.redditclient.post.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import rx.Observable;

public final class Stub {

    private static final int UNKNOWN_CHILD_COUNT = -1;

    private final int childCount;
    @NonNull private final Observable<Boolean> trigger;

    private Stub(final Builder builder) {
        this.childCount = builder.childCount;
        this.trigger = builder.trigger;
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

    public static class Builder {

        @NonNull private final Observable<Boolean> trigger;

        private int childCount = UNKNOWN_CHILD_COUNT;

        public Builder(@NonNull final Observable<Boolean> trigger) {
            this.trigger = trigger;
        }

        public Builder childCount(@IntRange(from = 0) final int childCount) {
            this.childCount = childCount;
            return this;
        }

        @NonNull
        public Stub build() {
            return new Stub(this);
        }
    }
}
