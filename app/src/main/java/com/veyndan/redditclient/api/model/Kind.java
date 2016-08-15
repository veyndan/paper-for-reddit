package com.veyndan.redditclient.api.model;

import com.google.gson.annotations.SerializedName;

public enum Kind {
    @SerializedName("t1") COMMENT("t1", Comment.class),
    @SerializedName("t2") ACCOUNT("t2", Account2.class),
    @SerializedName("t3") LINK("t3", Link.class),
    @SerializedName("t4") MESSAGE("t4", Message.class),
    @SerializedName("t5") SUBREDDIT("t5", Subreddit.class),
    //@SerializedName("t6") AWARD("t6"),
    //@SerializedName("t7") PROMO_CAMPAIGN("t8"),
    @SerializedName("Listing") LISTING("Listing", Listing.class),
    @SerializedName("TrophyList") TROPHY_LIST("TrophyList", Trophies.class),
    @SerializedName("more") MORE("more", More.class);

    private final String type;
    private final Class clazz;

    Kind(String type, Class clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public Class getDerivedClass() {
        return clazz;
    }

    @Override
    public String toString() {
        return type;
    }
}
