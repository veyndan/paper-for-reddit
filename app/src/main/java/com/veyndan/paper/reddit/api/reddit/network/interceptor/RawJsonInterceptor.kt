package com.veyndan.paper.reddit.api.reddit.network.interceptor

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RawJsonInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val url: HttpUrl = request.url().newBuilder()
                .addQueryParameter("raw_json", "1")
                .build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
