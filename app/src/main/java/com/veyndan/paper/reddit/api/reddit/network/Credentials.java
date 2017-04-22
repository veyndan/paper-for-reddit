package com.veyndan.paper.reddit.api.reddit.network;

public final class Credentials {

    private final String clientId;
    private final String clientSecret;
    private final String userAgent;
    private final String username;
    private final String password;

    public Credentials(final String clientId, final String clientSecret, final String userAgent,
                       final String username, final String password) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userAgent = userAgent;
        this.username = username;
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static String createUserAgent(final String platform, final String appId,
                                         final String version, final String username) {
        return String.format("%s:%s:%s (by /u/%s)", platform, appId, version, username);
    }
}
