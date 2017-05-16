package com.veyndan.paper.reddit.api.xkcd.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class XkcdComic {

    public abstract String img();

    public static JsonAdapter<XkcdComic> jsonAdapter(final Moshi moshi) {
        return new AutoValue_XkcdComic.MoshiJsonAdapter(moshi);
    }
}
