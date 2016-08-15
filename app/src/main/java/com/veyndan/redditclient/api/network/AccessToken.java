package com.veyndan.redditclient.api.network;

import java.util.concurrent.TimeUnit;

public final class AccessToken {
    private String accessToken;
    private String tokenType;

    /**
     * The number of seconds that this access token is valid for.
     */
    private int expiresIn;
    private String scope;

    private final long timestamp;

    private AccessToken() {
        this.timestamp = System.currentTimeMillis();
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
