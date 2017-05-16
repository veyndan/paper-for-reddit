package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;

@AutoValue
public abstract class Listing implements RedditObject {

    public static Listing create() {
        return new AutoValue_Listing("", "", "", new ArrayList<>());
    }

    public abstract String before();

    public abstract String after();

    public abstract String modhash();

    public abstract List<RedditObject> children();

    public static JsonAdapter<Listing> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Listing.MoshiJsonAdapter(moshi);
    }
}
