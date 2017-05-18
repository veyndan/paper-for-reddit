package com.veyndan.paper.reddit.api.reddit.network.interceptor

import com.veyndan.paper.reddit.api.reddit.network.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthorizationInterceptor(private val credentials: Credentials) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val credential: String = okhttp3.Credentials.basic(credentials.clientId, credentials.clientSecret)
        val authenticatedRequest: Request = request.newBuilder()
                .header("Authorization", credential).build()
        return chain.proceed(authenticatedRequest)
    }
}
