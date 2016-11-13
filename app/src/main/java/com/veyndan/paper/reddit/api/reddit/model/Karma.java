package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Karma {

    public abstract String sr();

    public abstract int commentKarma();

    public abstract int linkKarma();

    public static TypeAdapter<Karma> typeAdapter(final Gson gson) {
        return new AutoValue_Karma.GsonTypeAdapter(gson);
    }
}
