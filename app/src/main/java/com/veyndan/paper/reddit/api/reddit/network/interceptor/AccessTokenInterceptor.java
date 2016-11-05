package com.veyndan.paper.reddit.api.reddit.network.interceptor;

import com.veyndan.paper.reddit.api.reddit.network.AccessToken;
import com.veyndan.paper.reddit.api.reddit.network.AuthenticationService;
import com.veyndan.paper.reddit.api.reddit.network.Credentials;

import java.io.IOException;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class AccessTokenInterceptor implements Interceptor {

    private final AuthenticationService authenticationService;
    private final Credentials credentials;

    private AccessToken accessTokenCache;

    public AccessTokenInterceptor(final AuthenticationService authenticationService,
                                  final Credentials credentials) {
        this.authenticationService = authenticationService;
        this.credentials = credentials;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request[] accessTokenRequest = {chain.request()};

        Maybe.concat(accessTokenCache(), accessTokenNetwork())
                .filter(accessToken -> !accessToken.isExpired())
                .firstElement()
                .subscribe(accessToken -> {
                    accessTokenRequest[0] = accessTokenRequest[0].newBuilder()
                            .header("Authorization", "Bearer " + accessToken.getAccessToken())
                            .build();
                });

        return chain.proceed(accessTokenRequest[0]);
    }

    private Maybe<AccessToken> accessTokenCache() {
        return accessTokenCache == null
                ? Maybe.empty()
                : Maybe.just(accessTokenCache);
    }

    private Maybe<AccessToken> accessTokenNetwork() {
        final Single<AccessToken> single = authenticationService.getAccessToken(
                "password", credentials.getUsername(), credentials.getPassword());

        // Save access token from network into the cache.
        return single.doOnSuccess(accessToken -> accessTokenCache = accessToken).toMaybe();
    }
}
