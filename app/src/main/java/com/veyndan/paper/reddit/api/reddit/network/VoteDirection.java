package com.veyndan.paper.reddit.api.reddit.network;

import com.google.gson.annotations.SerializedName;

public enum VoteDirection {
    @SerializedName("upvote")UPVOTE(1),
    @SerializedName("downvote")DOWNVOTE(-1),
    @SerializedName("unvote")UNVOTE(0);

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
