package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Trophy {

    public abstract String icon70();

    public abstract String description();

    public abstract String url();

    public abstract String icon40();

    public abstract String awardId();

    public abstract String id();

    public abstract String name();

    public static TypeAdapter<Trophy> typeAdapter(final Gson gson) {
        return new AutoValue_Trophy.GsonTypeAdapter(gson);
    }
}
