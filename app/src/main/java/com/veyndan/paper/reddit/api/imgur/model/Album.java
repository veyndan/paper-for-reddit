package com.veyndan.paper.reddit.api.imgur.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

@AutoValue
public abstract class Album {

    public abstract List<Image> images();

    public static TypeAdapter<Album> typeAdapter(final Gson gson) {
        return new AutoValue_Album.GsonTypeAdapter(gson);
    }
}
