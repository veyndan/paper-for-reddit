package com.veyndan.redditclient.api.reddit.network.interceptor;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RawJsonInterceptor implements Interceptor {

    @Override
    public Response intercept(final Chain chain) throws IOException {
        Request request = chain.request();
        final HttpUrl url = request.url().newBuilder()
                .addQueryParameter("raw_json", "1")
                .build();
        request = request.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
