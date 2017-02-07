package com.veyndan.paper.reddit.api.reddit.network;

import com.squareup.moshi.Json;

public enum VoteDirection {
    @Json(name = "upvote")UPVOTE(1),
    @Json(name = "downvote")DOWNVOTE(-1),
    @Json(name = "unvote")UNVOTE(0);

    private final int direction;

    VoteDirection(final int direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return String.valueOf(direction);
    }
}
