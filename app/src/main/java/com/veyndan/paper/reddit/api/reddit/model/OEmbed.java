package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class OEmbed {

    @Json(name = "provider_url")
    public abstract String providerUrl();

    public abstract String description();

    public abstract String title();

    public abstract String type();

    @Json(name = "thumbnail_width")
    public abstract int thumbnailWidth();

    public abstract int height();

    public abstract int width();

    public abstract String html();

    public abstract String version();

    @Json(name = "provider_name")
    public abstract String providerName();

    @Json(name = "thumbnail_url")
    public abstract String thumbnailUrl();

    @Json(name = "thumbnail_height")
    public abstract int thumbnailHeight();

    public static JsonAdapter<OEmbed> jsonAdapter(final Moshi moshi) {
        return new AutoValue_OEmbed.MoshiJsonAdapter(moshi);
    }
}
