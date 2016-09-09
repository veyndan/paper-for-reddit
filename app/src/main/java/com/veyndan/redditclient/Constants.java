package com.veyndan.redditclient;

import com.veyndan.redditclient.api.reddit.network.Credentials;

public final class Constants {

    // Suppress default constructor for noninstantiability
    private Constants() {
        throw new AssertionError();
    }

    public static final String REDDIT_REDIRECT_URI = "https://github.com/veyndan/reddit-client";
    public static final String REDDIT_USER_AGENT = Credentials.createUserAgent(
            "android", "com.veyndan.redditclient", "0.0.1", "VeyndanStuart");
}
