package com.veyndan.paper.reddit.api.reddit.network

enum class User {
    COMMENTS, DOWNVOTED, GILDED, HIDDEN, OVERVIEW, SAVED, SUBMITTED, UPVOTED;

    override fun toString(): String {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name.toLowerCase()
    }
}
