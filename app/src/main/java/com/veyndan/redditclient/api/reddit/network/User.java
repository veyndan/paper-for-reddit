package com.veyndan.redditclient.api.reddit.network;

public enum User {
    COMMENTS, DOWNVOTED, GILDED, HIDDEN, OVERVIEW, SAVED, SUBMITTED, UPVOTED;

    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
