package com.veyndan.paper.reddit.api.reddit.network;

import android.support.annotation.NonNull;

public enum SubredditSort {
    DEFAULT, GOLD, NEW, POPULAR;

    @NonNull
    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
