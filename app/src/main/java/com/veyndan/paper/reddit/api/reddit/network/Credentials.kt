package com.veyndan.paper.reddit.api.reddit.network;


data class Credentials(val clientId: String, val clientSecret: String, val userAgent: String,
                       val username: String, val password: String) {

    companion object {

        @JvmStatic
        fun createUserAgent(platform: String, appId: String, version: String, username: String): String = "$platform:$appId:$version (by /u/$username)"
    }
}
