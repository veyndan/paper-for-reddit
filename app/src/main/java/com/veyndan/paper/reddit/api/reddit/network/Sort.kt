package com.veyndan.paper.reddit.api.reddit.network

import com.squareup.moshi.Json

enum class Sort {
    @Json(name = "hot") HOT,
    @Json(name = "new") NEW,
    @Json(name = "rising") RISING,
    @Json(name = "controversial") CONTROVERSIAL,
    @Json(name = "top") TOP;

    override fun toString(): String {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name.toLowerCase()
    }
}
