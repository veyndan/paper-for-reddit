package com.veyndan.paper.reddit.api.reddit.network.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RawJsonInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        Request request = chain.request();
        final HttpUrl url = request.url().newBuilder()
                .addQueryParameter("raw_json", "1")
                .build();
        request = request.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
