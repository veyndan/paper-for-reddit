package com.veyndan.paper.reddit.api.reddit.model

data class Listing(val after: String? = null, val children: MutableList<RedditObject> = ArrayList()) : RedditObject
