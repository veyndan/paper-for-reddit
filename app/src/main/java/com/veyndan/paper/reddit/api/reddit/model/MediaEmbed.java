package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class MediaEmbed {

    public abstract String content();

    public abstract int width();

    public abstract boolean scrolling();

    public abstract int height();

    public static TypeAdapter<MediaEmbed> typeAdapter(final Gson gson) {
        return new AutoValue_MediaEmbed.GsonTypeAdapter(gson);
    }
}
