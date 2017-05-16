package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class Json {

    public abstract List<Object> errors();

    public abstract Things data();

    public static JsonAdapter<Json> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Json.MoshiJsonAdapter(moshi);
    }
}
