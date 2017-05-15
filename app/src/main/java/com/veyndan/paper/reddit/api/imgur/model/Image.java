package com.veyndan.paper.reddit.api.imgur.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Image {

    public abstract int width();
    public abstract int height();
    public abstract String link();

    public static JsonAdapter<Image> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Image.MoshiJsonAdapter(moshi);
    }
}
