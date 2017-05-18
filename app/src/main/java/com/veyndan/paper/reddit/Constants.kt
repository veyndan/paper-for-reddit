package com.veyndan.paper.reddit

class Constants private constructor() {

    init {
        throw AssertionError("No instances.")
    }

    companion object {

        val REDDIT_REDIRECT_URI = "https://github.com/veyndan/paper-for-reddit"
    }
}
