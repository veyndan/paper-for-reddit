package com.veyndan.paper.reddit;

import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.api.reddit.network.Credentials;

public final class Constants {

    // Suppress default constructor for noninstantiability
    private Constants() {
        throw new AssertionError();
    }

    @NonNull public static final String REDDIT_REDIRECT_URI = "https://github.com/veyndan/reddit-client";
    @NonNull public static final String REDDIT_USER_AGENT = Credentials.createUserAgent(
            "android", "com.veyndan.paper.reddit", "0.0.1", "VeyndanStuart");
}
