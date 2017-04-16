package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public class Account2 extends RedditObject implements Created {

    private long created;
    @Json(name = "created_utc") private long createdUtc;

    // Documented
    @Json(name = "comment_karma") public int commentKarma;
    @Json(name = "has_mail") public boolean hasMail;
    @Json(name = "has_mod_mail") public boolean hasModMail;
    @Json(name = "has_verified_email") public boolean hasVerifiedEmail;
    public String id;
    @Json(name = "inbox_count") public int inboxCount;
    @Json(name = "is_friend") public boolean isFriend;
    @Json(name = "is_gold") public boolean isGold;
    @Json(name = "is_mod") public boolean isMod;
    @Json(name = "link_karma") public int linkKarma;
    public String modhash;
    public String name;
    @Json(name = "over_18") public boolean over18;

    // Undocumented
    @Json(name = "is_employee") public boolean isEmployee;
    @Json(name = "hide_from_robots") public boolean hideFromRobots;
    @Json(name = "is_suspended") public boolean isSuspended;
    @Json(name = "in_beta") public boolean inBeta;
    public Features features;
    @Json(name = "gold_expiration") public Object goldExpiration;
    @Json(name = "gold_creddits") public int goldCreddits;
    @Json(name = "suspension_expiration_utc") public Object suspensionExpirationUtc; // Type not sure

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public long getCreatedUtc() {
        return createdUtc;
    }
}
