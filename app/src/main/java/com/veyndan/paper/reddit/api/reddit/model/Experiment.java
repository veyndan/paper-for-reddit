package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Experiment {

    public abstract String variant();

    @Json(name = "experiment_id")
    public abstract int experimentId();

    public static JsonAdapter<Experiment> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Experiment.MoshiJsonAdapter(moshi);
    }
}
