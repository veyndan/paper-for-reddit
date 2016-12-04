package com.veyndan.paper.reddit.api.reddit.network;

import android.support.annotation.NonNull;

public final class Credentials {
    @NonNull private final String clientId;
    @NonNull private final String clientSecret;
    @NonNull private final String userAgent;
    @NonNull private final String username;
    @NonNull private final String password;

    public Credentials(@NonNull final String clientId, @NonNull final String clientSecret,
                       @NonNull final String userAgent, @NonNull final String username,
                       @NonNull final String password) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userAgent = userAgent;
        this.username = username;
        this.password = password;
    }

    @NonNull
    public String getClientId() {
        return clientId;
    }

    @NonNull
    public String getClientSecret() {
        return clientSecret;
    }

    @NonNull
    public String getUserAgent() {
        return userAgent;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    @NonNull
    public static String createUserAgent(@NonNull final String platform, @NonNull final String appId,
                                         @NonNull final String version, @NonNull final String username) {
        return String.format("%s:%s:%s (by /u/%s)", platform, appId, version, username);
    }
}
