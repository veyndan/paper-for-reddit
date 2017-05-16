package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public interface Created {

    long created();

    @Json(name = "created_utc")
    long createdUtc();
}
