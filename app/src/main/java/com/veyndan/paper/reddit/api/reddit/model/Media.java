package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Media {

    @Json(name = "o_embed")
    public abstract OEmbed oEmbed();

    public abstract String type();

    public static JsonAdapter<Media> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Media.MoshiJsonAdapter(moshi);
    }
}
