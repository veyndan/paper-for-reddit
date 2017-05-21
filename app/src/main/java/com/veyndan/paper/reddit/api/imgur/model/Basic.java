package com.veyndan.paper.reddit.api.imgur.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class Basic<T> {

    @Nullable private T data;

    @NonNull
    public T getData() {
        return checkNotNull(data);
    }
}
