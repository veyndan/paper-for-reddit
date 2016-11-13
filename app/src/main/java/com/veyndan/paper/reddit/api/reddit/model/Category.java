package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Category {

    public abstract String category();

    public static TypeAdapter<Category> typeAdapter(final Gson gson) {
        return new AutoValue_Category.GsonTypeAdapter(gson);
    }
}
