package com.veyndan.redditclient.api.network;

public enum SubredditSort {
    DEFAULT, GOLD, NEW, POPULAR;

    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
