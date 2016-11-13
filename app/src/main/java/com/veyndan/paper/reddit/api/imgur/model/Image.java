package com.veyndan.paper.reddit.api.imgur.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Image {

    public abstract int width();

    public abstract int height();

    public abstract String link();

    public static TypeAdapter<Image> typeAdapter(final Gson gson) {
        return new AutoValue_Image.GsonTypeAdapter(gson);
    }
}
