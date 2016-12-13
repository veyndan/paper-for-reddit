package com.veyndan.paper.reddit.api.reddit.network;

import com.google.gson.annotations.SerializedName;

public enum MySubreddits {
    @SerializedName("subscriber")SUBSCRIBER,
    @SerializedName("contributor")CONTRIBUTOR,
    @SerializedName("moderator")MODERATOR;

    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
