package com.veyndan.redditclient.post.model;

import android.support.annotation.IntRange;

public class Stub {

    private static final int UNKNOWN_CHILD_COUNT = -1;
    private final int childCount;

    public Stub() {
        this.childCount = UNKNOWN_CHILD_COUNT;
    }

    public Stub(@IntRange(from = 0) final int childCount) {
        this.childCount = childCount;
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
}
