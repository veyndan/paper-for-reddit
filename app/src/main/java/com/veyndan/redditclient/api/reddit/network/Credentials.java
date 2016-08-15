package com.veyndan.redditclient.api.reddit.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.Scanner;

public final class Credentials {
    private String clientId;
    private String clientSecret;
    private UserAgent userAgent;
    private String username;
    private String password;

    private Credentials() {
    }

    public static Credentials create(final InputStream inputStream) {
        return fromJson(convertStreamToString(inputStream));
    }

    private static Credentials fromJson(final String json) {
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return gson.fromJson(json, Credentials.class);
    }

    // http://stackoverflow.com/a/5445161
    private static String convertStreamToString(final InputStream is) {
        final Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getUserAgent() {
        return userAgent.string();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private static final class UserAgent {
        private String platform;
        private String appId;
        private String version;
        private String username;

        String string() {
            return String.format("%s:%s:%s (by /u/%s)", platform, appId, version, username);
        }
    }
}
