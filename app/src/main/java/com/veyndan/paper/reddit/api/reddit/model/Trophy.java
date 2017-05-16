package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Trophy {

    @Json(name = "icon_70")
    public abstract String icon70();

    public abstract String description();

    public abstract String url();

    @Json(name = "icon_40")
    public abstract String icon40();

    @Json(name = "award_id")
    public abstract String awardId();

    public abstract String id();

    public abstract String name();

    public static JsonAdapter<Trophy> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Trophy.MoshiJsonAdapter(moshi);
    }
}
