package com.veyndan.paper.reddit.api.reddit.network;

public final class Credentials {

    private final String clientId;
    private final String userAgent;

    public Credentials(final String clientId, final String userAgent) {
        this.clientId = clientId;
        this.userAgent = userAgent;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public static String createUserAgent(final String platform, final String appId,
                                         final String version, final String username) {
        return String.format("%s:%s:%s (by /u/%s)", platform, appId, version, username);
    }
}
