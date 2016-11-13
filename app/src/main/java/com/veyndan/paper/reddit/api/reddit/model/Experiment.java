package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Experiment {

    public abstract String variant();

    public abstract int experimentId();

    public static TypeAdapter<Experiment> typeAdapter(final Gson gson) {
        return new AutoValue_Experiment.GsonTypeAdapter(gson);
    }
}
