package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public enum Kind {
    @Json(name = "t1")COMMENT("t1", Comment.class),
    @Json(name = "t3")LINK("t3", Link.class),
    @Json(name = "Listing")LISTING("Listing", Listing.class),
    @Json(name = "more")MORE("more", More.class);

    private final String type;
    private final Class<? extends RedditObject> clazz;

    Kind(final String type, final Class<? extends RedditObject> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public Class<? extends RedditObject> getDerivedClass() {
        return clazz;
    }

    @Override
    public String toString() {
        return type;
    }
}
