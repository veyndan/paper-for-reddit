package com.veyndan.paper.reddit.api.reddit.network

import com.squareup.moshi.Json

import java.util.concurrent.TimeUnit

class AccessToken(
        @Json(name = "access_token") val accessToken: String? = null,
        @Json(name = "token_type") val tokenType: String? = null,

        /**
         * The number of seconds that this access token is valid for.
         */
        @Json(name = "expires_in") private val expiresIn: Long = -1,

        val scope: String? = null
) {

    companion object {

        val EXPIRED_ACCESS_TOKEN: AccessToken = AccessToken()
    }

    private val timestamp: Long = System.currentTimeMillis()

    /**
     * The number of milliseconds that this access token is valid for.
     */
    private fun expiresIn(): Long {
        return TimeUnit.MILLISECONDS.convert(expiresIn, TimeUnit.SECONDS)
    }

    fun isExpired(): Boolean {
        return timestamp + expiresIn() < System.currentTimeMillis()
    }
}
