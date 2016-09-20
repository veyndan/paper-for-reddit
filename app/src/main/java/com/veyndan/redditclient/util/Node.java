package com.veyndan.redditclient.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

public class Node<T> {

    @NonNull private final T data;
    @IntRange(from = 0) private final int depth;

    public Node(@NonNull final T data) {
        this(data, 0);
    }

    public Node(@NonNull final T data, @IntRange(from = 0) final int depth) {
        this.data = data;
        this.depth = depth;
    }

    @NonNull
    public T getData() {
        return data;
    }

    @IntRange(from = 0)
    public int getDepth() {
        return depth;
    }
}
