package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.Nullable;

public class Thing<T> {
    @Nullable public Kind kind;
    @Nullable public T data;

    public Thing(@Nullable final T data) {
        this.data = data;
    }
}
