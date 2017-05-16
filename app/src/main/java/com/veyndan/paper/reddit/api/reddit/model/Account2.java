package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Account2 implements Created, RedditObject {

    // Documented
    @Json(name = "comment_karma")
    public abstract int commentKarma();

    @Json(name = "has_mail")
    public abstract boolean hasMail();

    @Json(name = "has_mod_mail")
    public abstract boolean hasModMail();

    @Json(name = "has_verified_email")
    public abstract boolean hasVerifiedEmail();

    public abstract String id();

    @Json(name = "inbox_count")
    public abstract int inboxCount();

    @Json(name = "is_friend")
    public abstract boolean isFriend();

    @Json(name = "is_gold")
    public abstract boolean isGold();

    @Json(name = "is_mod")
    public abstract boolean isMod();

    @Json(name = "link_karma")
    public abstract int linkKarma();

    public abstract String modhash();

    public abstract String name();

    @Json(name = "over_18")
    public abstract boolean over18();

    // Undocumented
    @Json(name = "is_employee")
    public abstract boolean isEmployee();

    @Json(name = "hide_from_robots")
    public abstract boolean hideFromRobots();

    @Json(name = "is_suspended")
    public abstract boolean isSuspended();

    @Json(name = "in_beta")
    public abstract boolean inBeta();

    public abstract Features features();

    @Json(name = "gold_expiration")
    public abstract Object goldExpiration();

    @Json(name = "gold_creddits")
    public abstract int goldCreddits();

    @Json(name = "suspension_expiration_utc")
    public abstract Object suspensionExpirationUtc(); // Type not sure

    public static JsonAdapter<Account2> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Account2.MoshiJsonAdapter(moshi);
    }
}
