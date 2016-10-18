package com.veyndan.paper.reddit.api.reddit.model;

import com.google.gson.annotations.SerializedName;

public enum Distinguished {

    /**
     * The green [M].
     */
    @SerializedName("moderator") MODERATOR,

    /**
     * The red [A].
     */
    @SerializedName("admin") ADMIN,

    /**
     * Various other special distinguishes (most commonly seen as the darker red [Δ]
     * "admin emeritus" - <a href="https://www.reddit.com/r/bestof/comments/175prt/alilarter_connects_with_a_user_who_has_a/c82tlns">example</a>)
     */
    @SerializedName("special") SPECIAL
}
