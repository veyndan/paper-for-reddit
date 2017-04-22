package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public class Experiment {

    public String variant;
    @Json(name = "experiment_id") public int experimentId;
}
