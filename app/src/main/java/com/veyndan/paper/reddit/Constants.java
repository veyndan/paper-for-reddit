package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.api.reddit.network.Credentials;

public final class Constants {

    private Constants() {
        throw new AssertionError("No instances.");
    }

    public static final String REDDIT_REDIRECT_URI = "https://github.com/veyndan/paper-for-reddit";
    public static final String REDDIT_USER_AGENT = Credentials.createUserAgent(
            "android", "com.veyndan.paper.reddit", "0.0.1", "VeyndanStuart");
}
