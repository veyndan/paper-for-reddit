package com.veyndan.paper.reddit.api.reddit.network.interceptor

import com.veyndan.paper.reddit.api.reddit.network.AccessToken
import com.veyndan.paper.reddit.api.reddit.network.AuthenticationService
import com.veyndan.paper.reddit.api.reddit.network.Credentials
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AccessTokenInterceptor(private val authenticationService: AuthenticationService,
                             private val credentials: Credentials) : Interceptor {

    private var accessTokenCache = AccessToken.EXPIRED_ACCESS_TOKEN

    override fun intercept(chain: Interceptor.Chain): Response {
        val unauthorizedRequest: Request = chain.request()

        val authorizedRequest: Request = Single.concat(accessTokenCache(), accessTokenNetwork())
                .filter { accessToken -> !accessToken.isExpired() }
                .firstElement()
                .map { accessToken ->
                    unauthorizedRequest.newBuilder()
                            .header("Authorization", "Bearer ${accessToken.accessToken}")
                            .build()
                }
                .blockingGet()

        return chain.proceed(authorizedRequest)
    }

    private fun accessTokenCache(): Single<AccessToken> {
        return Single.just(accessTokenCache)
    }

    private fun accessTokenNetwork(): Single<AccessToken> {
        val single: Single<AccessToken> = authenticationService.getAccessToken(
                "password", credentials.username, credentials.password)
                .map { it.body()!! }

        // Save access token from network into the cache.
        return single.doOnSuccess { accessToken -> accessTokenCache = accessToken }
    }
}
