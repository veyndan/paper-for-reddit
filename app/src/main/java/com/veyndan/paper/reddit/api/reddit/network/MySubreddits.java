package com.veyndan.paper.reddit.api.reddit.network;

import com.squareup.moshi.Json;

public enum MySubreddits {
    @Json(name = "subscriber")SUBSCRIBER,
    @Json(name = "contributor")CONTRIBUTOR,
    @Json(name = "moderator")MODERATOR;

    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
