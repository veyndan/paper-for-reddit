package com.veyndan.paper.reddit.api.reddit.network.interceptor;

import com.veyndan.paper.reddit.api.reddit.network.Credentials;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    private final Credentials credentials;

    public AuthorizationInterceptor(final Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();
        final String credential = okhttp3.Credentials.basic(credentials.getClientId(), credentials.getClientSecret());
        final Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credential).build();
        return chain.proceed(authenticatedRequest);
    }
}
