package com.veyndan.paper.reddit.api.reddit.network

import com.squareup.moshi.Json

enum class TimePeriod {
    @Json(name = "hour") HOUR,
    @Json(name = "day") DAY,
    @Json(name = "week") WEEK,
    @Json(name = "month") MONTH,
    @Json(name = "year") YEAR,
    @Json(name = "all") ALL;

    override fun toString(): String {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name.toLowerCase()
    }
}
