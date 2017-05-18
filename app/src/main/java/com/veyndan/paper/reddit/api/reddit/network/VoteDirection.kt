package com.veyndan.paper.reddit.api.reddit.network

import com.squareup.moshi.Json

enum class VoteDirection constructor(private val direction: Int) {
    @Json(name = "upvote") UPVOTE(1),
    @Json(name = "downvote") DOWNVOTE(-1),
    @Json(name = "unvote") UNVOTE(0);

    override fun toString(): String {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return direction.toString()
    }
}
