package com.veyndan.paper.reddit.api.imgur.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class Album {

    public abstract List<Image> images();

    public static JsonAdapter<Album> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Album.MoshiJsonAdapter(moshi);
    }
}
