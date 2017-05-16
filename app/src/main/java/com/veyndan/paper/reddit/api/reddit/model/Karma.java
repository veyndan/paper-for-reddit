package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Karma {

    public abstract String sr();

    @Json(name = "comment_karma")
    public abstract int commentKarma();

    @Json(name = "link_karma")
    public abstract int linkKarma();

    public static JsonAdapter<Karma> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Karma.MoshiJsonAdapter(moshi);
    }
}
