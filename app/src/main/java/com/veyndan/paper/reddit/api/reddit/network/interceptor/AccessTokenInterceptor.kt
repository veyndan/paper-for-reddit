package com.veyndan.paper.reddit.api.reddit.network.interceptor

import com.veyndan.paper.reddit.api.reddit.network.AccessToken
import com.veyndan.paper.reddit.api.reddit.network.AuthenticationService
import com.veyndan.paper.reddit.api.reddit.network.Credentials
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Timed
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AccessTokenInterceptor(private val authenticationService: AuthenticationService,
                             private val credentials: Credentials) : Interceptor {

    private var accessTokenCache: Single<Timed<AccessToken>> = Observable.just(AccessToken.EXPIRED_ACCESS_TOKEN)
            .timestamp()
            .singleOrError()

    override fun intercept(chain: Interceptor.Chain): Response {
        val unauthorizedRequest: Request = chain.request()

        val authorizedRequest: Request = Single.concat(accessTokenCache, accessTokenNetwork())
                .filter { timed -> timed.time() + timed.value().expiresIn() >= System.currentTimeMillis() }
                .firstElement()
                .map { timed ->
                    unauthorizedRequest.newBuilder()
                            .header("Authorization", "Bearer ${timed.value().accessToken}")
                            .build()
                }
                .blockingGet()

        return chain.proceed(authorizedRequest)
    }

    private fun accessTokenNetwork(): Single<Timed<AccessToken>> {
        val single: Single<Timed<AccessToken>> = authenticationService.getAccessToken(
                "password", credentials.username, credentials.password)
                .map { it.body()!! }
                .toObservable()
                .timestamp()
                .singleOrError()

        // Save access token from network into the cache.
        return single.doOnSuccess { accessToken -> accessTokenCache = Single.just(accessToken) }
    }
}
