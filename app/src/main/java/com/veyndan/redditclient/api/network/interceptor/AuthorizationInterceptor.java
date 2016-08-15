package com.veyndan.redditclient.api.network.interceptor;

import com.veyndan.redditclient.api.network.Credentials;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    private Credentials credentials;

    public AuthorizationInterceptor(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String credential = okhttp3.Credentials.basic(credentials.getClientId(), credentials.getClientSecret());
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credential).build();
        return chain.proceed(authenticatedRequest);
    }
}
