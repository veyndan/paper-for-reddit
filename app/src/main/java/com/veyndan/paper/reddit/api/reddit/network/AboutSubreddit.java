package com.veyndan.paper.reddit.api.reddit.network;

public enum AboutSubreddit {
    BANNED, CONTRIBUTORS, MODERATORS, MUTED, WIKIBANNED, WIKICONTRIBUTORS;

    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
