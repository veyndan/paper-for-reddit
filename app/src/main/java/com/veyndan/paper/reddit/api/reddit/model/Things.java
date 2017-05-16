package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class Things {

    public abstract List<RedditObject> things();

    public static JsonAdapter<Things> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Things.MoshiJsonAdapter(moshi);
    }
}
