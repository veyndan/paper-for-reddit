package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

public class Thing<T> {

    @Nullable private Kind kind;
    @Nullable private final T data;

    public Thing(@NonNull final T data) {
        this.data = data;
    }

    @NonNull
    public Kind getKind() {
        return Objects.requireNonNull(kind);
    }

    @NonNull
    public T getData() {
        return Objects.requireNonNull(data);
    }
}
