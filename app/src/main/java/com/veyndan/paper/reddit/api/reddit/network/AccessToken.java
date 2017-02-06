package com.veyndan.paper.reddit.api.reddit.network;

import com.squareup.moshi.Json;

import java.util.concurrent.TimeUnit;

public final class AccessToken {

    public static final AccessToken EXPIRED_ACCESS_TOKEN;

    static {
        EXPIRED_ACCESS_TOKEN = new AccessToken();
        EXPIRED_ACCESS_TOKEN.expiresIn = -1;
    }

    @Json(name = "access_token") private String accessToken;
    @Json(name = "token_type") private String tokenType;

    /**
     * The number of seconds that this access token is valid for.
     */
    @Json(name = "expires_in") private int expiresIn;
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
