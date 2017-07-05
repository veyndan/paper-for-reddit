package com.veyndan.paper.reddit.api.reddit.network.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class UserAgentInterceptor(private val userAgentHeaderValue: String) : Interceptor {

    companion object {

        private const val USER_AGENT_HEADER_NAME = "User-Agent"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val requestWithUserAgent: Request = originalRequest.newBuilder()
                .header(USER_AGENT_HEADER_NAME, userAgentHeaderValue)
                .build()
        return chain.proceed(requestWithUserAgent)
    }
}
