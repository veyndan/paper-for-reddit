package com.veyndan.paper.reddit.api.reddit.network;

import com.google.gson.annotations.SerializedName;

public enum TimePeriod {
    @SerializedName("hour") HOUR,
    @SerializedName("day") DAY,
    @SerializedName("week") WEEK,
    @SerializedName("month") MONTH,
    @SerializedName("year") YEAR,
    @SerializedName("all") ALL;

    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
