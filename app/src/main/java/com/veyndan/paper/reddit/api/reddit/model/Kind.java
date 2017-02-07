package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public enum Kind {
    @Json(name = "t1")COMMENT("t1", Comment.class),
    @Json(name = "t2")ACCOUNT("t2", Account2.class),
    @Json(name = "t3")LINK("t3", Link.class),
    @Json(name = "t4")MESSAGE("t4", Message.class),
    @Json(name = "t5")SUBREDDIT("t5", Subreddit.class),
    //@Json(name = "t6")AWARD("t6"),
    //@Json(name = "t7")PROMO_CAMPAIGN("t8"),
    @Json(name = "Listing")LISTING("Listing", Listing.class),
    @Json(name = "TrophyList")TROPHY_LIST("TrophyList", Trophies.class),
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
