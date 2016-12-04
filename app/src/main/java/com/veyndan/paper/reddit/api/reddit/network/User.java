package com.veyndan.paper.reddit.api.reddit.network;

import android.support.annotation.NonNull;

public enum User {
    COMMENTS, DOWNVOTED, GILDED, HIDDEN, OVERVIEW, SAVED, SUBMITTED, UPVOTED;

    @NonNull
    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
