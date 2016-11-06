package com.veyndan.paper.reddit.api.reddit.network;

import java.util.concurrent.TimeUnit;

public final class AccessToken {

    public static final AccessToken EXPIRED_ACCESS_TOKEN;

    static {
        EXPIRED_ACCESS_TOKEN = new AccessToken();
        EXPIRED_ACCESS_TOKEN.expiresIn = -1;
    }

    private String accessToken;
    private String tokenType;

    /**
     * The number of seconds that this access token is valid for.
     */
    private int expiresIn;
    private String scope;

    private final long timestamp;

    private AccessToken() {
        timestamp = System.currentTimeMillis();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    /**
     * The number of milliseconds that this access token is valid for.
     */
    public long getExpiresIn() {
        return TimeUnit.MILLISECONDS.convert(expiresIn, TimeUnit.SECONDS);
    }

    public String getScope() {
        return scope;
    }

    public boolean isExpired() {
        return timestamp + getExpiresIn() < System.currentTimeMillis();
    }
}
