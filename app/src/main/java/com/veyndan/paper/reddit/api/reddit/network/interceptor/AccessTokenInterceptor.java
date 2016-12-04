package com.veyndan.paper.reddit.api.reddit.network.interceptor;

import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.api.reddit.network.AccessToken;
import com.veyndan.paper.reddit.api.reddit.network.AuthenticationService;
import com.veyndan.paper.reddit.api.reddit.network.Credentials;

import java.io.IOException;

import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class AccessTokenInterceptor implements Interceptor {

    @NonNull private final AuthenticationService authenticationService;
    @NonNull private final Credentials credentials;

    @NonNull private AccessToken accessTokenCache = AccessToken.EXPIRED_ACCESS_TOKEN;

    public AccessTokenInterceptor(@NonNull final AuthenticationService authenticationService,
                                  @NonNull final Credentials credentials) {
        this.authenticationService = authenticationService;
        this.credentials = credentials;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
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

    @NonNull
    private Single<AccessToken> accessTokenCache() {
        return Single.just(accessTokenCache);
    }

    @NonNull
    private Single<AccessToken> accessTokenNetwork() {
        final Single<AccessToken> single = authenticationService.getAccessToken(
                "password", credentials.getUsername(), credentials.getPassword());

        // Save access token from network into the cache.
        return single.doOnSuccess(accessToken -> accessTokenCache = accessToken);
    }
}
