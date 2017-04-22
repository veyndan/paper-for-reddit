package com.veyndan.paper.reddit.api.reddit.network;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Credentials {

    public static Credentials create(final String clientId, final String clientSecret,
                                     final String userAgent, final String username,
                                     final String password) {
        return new AutoValue_Credentials(clientId, clientSecret, userAgent, username, password);
    }

    public abstract String clientId();

    public abstract String clientSecret();

    public abstract String userAgent();

    public abstract String username();

    public abstract String password();

    public static String createUserAgent(final String platform, final String appId,
                                         final String version, final String username) {
        return String.format("%s:%s:%s (by /u/%s)", platform, appId, version, username);
    }
}
