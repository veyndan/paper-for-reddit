package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class Image {

    public abstract Source source();

    public abstract List<Source> resolutions();

    public abstract Object variants();

    public abstract String id();

    public static JsonAdapter<Image> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Image.MoshiJsonAdapter(moshi);
    }
}
