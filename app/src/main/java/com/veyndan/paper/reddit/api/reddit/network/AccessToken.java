package com.veyndan.paper.reddit.api.reddit.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

public final class AccessToken {

    @NonNull public static final AccessToken EXPIRED_ACCESS_TOKEN;

    static {
        EXPIRED_ACCESS_TOKEN = new AccessToken();
        EXPIRED_ACCESS_TOKEN.expiresIn = -1;
    }

    @Nullable private String accessToken;
    @Nullable private String tokenType;

    /**
     * The number of seconds that this access token is valid for.
     */
    private int expiresIn;
    @Nullable private String scope;

    private final long timestamp;

    private AccessToken() {
        timestamp = System.currentTimeMillis();
    }

    @Nullable
    public String getAccessToken() {
        return accessToken;
    }

    @Nullable
    public String getTokenType() {
        return tokenType;
    }

    /**
     * The number of milliseconds that this access token is valid for.
     */
    public long getExpiresIn() {
        return TimeUnit.MILLISECONDS.convert(expiresIn, TimeUnit.SECONDS);
    }

    @Nullable
    public String getScope() {
        return scope;
    }

    public boolean isExpired() {
        return timestamp + getExpiresIn() < System.currentTimeMillis();
    }
}
