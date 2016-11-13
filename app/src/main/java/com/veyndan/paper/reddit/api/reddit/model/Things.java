package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

@AutoValue
public abstract class Things {

    public abstract List<RedditObject> things();

    public static TypeAdapter<Things> typeAdapter(final Gson gson) {
        return new AutoValue_Things.GsonTypeAdapter(gson);
    }
}
