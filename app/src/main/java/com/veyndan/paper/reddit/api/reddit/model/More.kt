package com.veyndan.paper.reddit.api.reddit.model

data class More(val count: Int, val id: String, val name: String, val children: MutableList<String> = ArrayList()) : RedditObject
