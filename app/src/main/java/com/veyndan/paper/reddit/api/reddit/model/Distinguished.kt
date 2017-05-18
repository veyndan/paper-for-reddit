package com.veyndan.paper.reddit.api.reddit.model

import com.squareup.moshi.Json

enum class Distinguished {

    /**
     * The green [M].
     */
    @Json(name = "moderator") MODERATOR,

    /**
     * The red [A].
     */
    @Json(name = "admin") ADMIN,

    /**
     * Various other special distinguishes (most commonly seen as the darker red [Δ]
     * "admin emeritus" - [example](https://www.reddit.com/r/bestof/comments/175prt/alilarter_connects_with_a_user_who_has_a/c82tlns))
     */
    @Json(name = "special") SPECIAL
}
