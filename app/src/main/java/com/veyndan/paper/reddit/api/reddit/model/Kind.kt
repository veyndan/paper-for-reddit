package com.veyndan.paper.reddit.api.reddit.model

import com.squareup.moshi.Json

enum class Kind constructor(private val type: String, val derivedClass: Class<out RedditObject>) {
    @Json(name = "t1") COMMENT("t1", Comment::class.java),
    @Json(name = "t3") LINK("t3", Link::class.java),
    @Json(name = "Listing") LISTING("Listing", Listing::class.java),
    @Json(name = "more") MORE("more", More::class.java);

    override fun toString(): String {
        return type
    }
}
