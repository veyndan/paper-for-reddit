package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class MoreChildren {

    public abstract Json json();

    public static JsonAdapter<MoreChildren> jsonAdapter(final Moshi moshi) {
        return new AutoValue_MoreChildren.MoshiJsonAdapter(moshi);
    }
}
