package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

@AutoValue
public abstract class Json {

    public abstract List<Object> errors();

    public abstract Things data();

    public static TypeAdapter<Json> typeAdapter(final Gson gson) {
        return new AutoValue_Json.GsonTypeAdapter(gson);
    }
}
