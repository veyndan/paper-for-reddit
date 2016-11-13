package com.veyndan.paper.reddit.api.imgur.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

@AutoValue
public abstract class Basic<T> {

    public abstract T data();

    public abstract boolean success();

    public abstract int status();

    public static <T> TypeAdapter<Basic<T>> typeAdapter(final Gson gson,
                                                        final TypeToken<? extends Basic<T>> typeToken) {
        return new AutoValue_Basic.GsonTypeAdapter(gson, typeToken);
    }
}
