package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Category {

    public abstract String category();

    public static JsonAdapter<Category> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Category.MoshiJsonAdapter(moshi);
    }
}
