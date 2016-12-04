package com.veyndan.paper.reddit.api.imgur.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

public class Basic<T> {

    @Nullable private T data;
    private boolean success;
    private int status;

    @NonNull
    public T getData() {
        return Objects.requireNonNull(data);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }
}
