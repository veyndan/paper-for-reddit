package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class Categories {

    public abstract List<Category> categories();

    public static JsonAdapter<Categories> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Categories.MoshiJsonAdapter(moshi);
    }
}
