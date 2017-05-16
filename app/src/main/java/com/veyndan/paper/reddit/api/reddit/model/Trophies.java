package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class Trophies implements RedditObject {

    public abstract List<Thing<Trophy>> trophies();

    public static JsonAdapter<Trophies> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Trophies.MoshiJsonAdapter(moshi);
    }
}
