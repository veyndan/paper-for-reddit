package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;

@AutoValue
public abstract class Thing<T> {

    public static <T> Thing<T> create(final T data) {
        return new AutoValue_Thing<>(null, data);
    }

    @Nullable
    public abstract Kind kind();

    public abstract T data();

    public static <T> JsonAdapter<Thing<T>> jsonAdapter(final Moshi moshi, final Type... types) {
        return new AutoValue_Thing.MoshiJsonAdapter<>(moshi, types);
    }
}
