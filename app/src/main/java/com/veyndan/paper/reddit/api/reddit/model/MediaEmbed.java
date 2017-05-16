package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class MediaEmbed {

    public abstract String content();

    public abstract int width();

    public abstract boolean scrolling();

    public abstract int height();

    public static JsonAdapter<MediaEmbed> jsonAdapter(final Moshi moshi) {
        return new AutoValue_MediaEmbed.MoshiJsonAdapter(moshi);
    }
}
