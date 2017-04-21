package com.veyndan.paper.reddit.api.reddit.network.interceptor;

import com.veyndan.paper.reddit.api.reddit.network.AccessToken;
import com.veyndan.paper.reddit.api.reddit.network.AuthenticationService;
import com.veyndan.paper.reddit.api.reddit.network.Credentials;

import java.io.IOException;

import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class AccessTokenInterceptor implements Interceptor {

    private final AuthenticationService authenticationService;
    private final Credentials credentials;

    private AccessToken accessTokenCache = AccessToken.EXPIRED_ACCESS_TOKEN;

    public AccessTokenInterceptor(final AuthenticationService authenticationService,
                                  final Credentials credentials) {
        this.authenticationService = authenticationService;
        this.credentials = credentials;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request unauthorizedRequest = chain.request();

        final Request authorizedRequest = Single.concat(accessTokenCache(), accessTokenNetwork())
                .filter(accessToken -> !accessToken.isExpired())
                .firstElement()
                .map(accessToken -> unauthorizedRequest.newBuilder()
                        .header("Authorization", "Bearer " + accessToken.getAccessToken())
                        .build())
                .blockingGet();

        return chain.proceed(authorizedRequest);
    }

    private Single<AccessToken> accessTokenCache() {
        return Single.just(accessTokenCache);
    }

    private Single<AccessToken> accessTokenNetwork() {
        final Single<AccessToken> single = authenticationService.getAccessToken(
                "password", credentials.getUsername(), credentials.getPassword())
                .map(retrofit2.Response::body);

        // Save access token from network into the cache.
        return single.doOnSuccess(accessToken -> accessTokenCache = accessToken);
    }
}
