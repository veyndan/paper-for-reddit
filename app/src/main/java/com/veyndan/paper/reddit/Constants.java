package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.api.reddit.network.Credentials;

public final class Constants {

    // Suppress default constructor for noninstantiability
    private Constants() {
        throw new AssertionError();
    }

    public static final String REDDIT_REDIRECT_URI_SCHEME = "veyndan";
    public static final String REDDIT_REDIRECT_URI_AUTHORITY = "com-veyndan-paper-reddit";

    public static final String REDDIT_REDIRECT_URI =
            REDDIT_REDIRECT_URI_SCHEME + "://" + REDDIT_REDIRECT_URI_AUTHORITY;

    public static final String REDDIT_USER_AGENT = Credentials.createUserAgent(
            "android", "com.veyndan.paper.reddit", "0.0.1", "VeyndanStuart");
}
