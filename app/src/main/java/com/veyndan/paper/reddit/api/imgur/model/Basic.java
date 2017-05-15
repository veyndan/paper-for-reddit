package com.veyndan.paper.reddit.api.imgur.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;

@AutoValue
public abstract class Basic<T> {

    public abstract T data();
    public abstract boolean success();
    public abstract int status();

    public static <T> JsonAdapter<Basic<T>> jsonAdapter(final Moshi moshi, final Type... types) {
        return new AutoValue_Basic.MoshiJsonAdapter<>(moshi, types);
    }
}
