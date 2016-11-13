package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Media {

    public abstract OEmbed oEmbed();

    public abstract String type();

    public static TypeAdapter<Media> typeAdapter(final Gson gson) {
        return new AutoValue_Media.GsonTypeAdapter(gson);
    }
}
