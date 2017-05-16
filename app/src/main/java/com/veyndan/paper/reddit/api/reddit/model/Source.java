package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Source {

    public abstract String url();

    public abstract int width();

    public abstract int height();

    public static JsonAdapter<Source> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Source.MoshiJsonAdapter(moshi);
    }
}
