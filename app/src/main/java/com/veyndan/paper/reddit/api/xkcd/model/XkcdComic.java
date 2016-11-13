package com.veyndan.paper.reddit.api.xkcd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class XkcdComic {

    public abstract String img();

    public static TypeAdapter<XkcdComic> typeAdapter(final Gson gson) {
        return new AutoValue_XkcdComic.GsonTypeAdapter(gson);
    }
}
