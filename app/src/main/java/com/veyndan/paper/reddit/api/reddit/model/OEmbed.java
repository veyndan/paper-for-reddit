package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class OEmbed {

    public abstract String providerUrl();

    public abstract String description();

    public abstract String title();

    public abstract String type();

    public abstract int thumbnailWidth();

    public abstract int height();

    public abstract int width();

    public abstract String html();

    public abstract String version();

    public abstract String providerName();

    public abstract String thumbnailUrl();

    public abstract int thumbnailHeight();

    public static TypeAdapter<OEmbed> typeAdapter(final Gson gson) {
        return new AutoValue_OEmbed.GsonTypeAdapter(gson);
    }
}
