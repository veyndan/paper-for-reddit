package com.veyndan.paper.reddit;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.text.TextUtils;

import org.apache.commons.lang3.RandomStringUtils;

import okhttp3.HttpUrl;

public class UserAuthentication {

    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";

    // https://www.reddit.com/api/v1/scopes
    private static final String[] SCOPES = {
            "edit", "flair", "history", "identity", "modconfig", "modflair", "modlog", "modposts",
            "modwiki", "mysubreddits", "privatemessages", "read", "report", "save", "submit",
            "subscribe", "vote", "wikiedit", "wikiread"};

    public static String authenticateUser(final Context context) {
        final String state = RandomStringUtils.randomAlphanumeric(16);

        final CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(final ComponentName componentName, final CustomTabsClient client) {
                final CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();

                // Force the intent to be opened in the Custom Tab, otherwise a "Complete action
                // using..." dialog is shown which can confuse users and is unhelpful as if the
                // correct activity isn't chosen, authentication won't be handled by this app.
                customTabsIntent.intent.setPackage(CUSTOM_TAB_PACKAGE_NAME);

                final HttpUrl url = HttpUrl.parse("https://www.reddit.com/api/v1/authorize.compact")
                        .newBuilder()
                        .addQueryParameter("client_id", Config.REDDIT_CLIENT_ID)
                        .addQueryParameter("response_type", "code")
                        .addQueryParameter("state", state)
                        .addQueryParameter("redirect_uri", Constants.REDDIT_REDIRECT_URI)
                        .addQueryParameter("duration", "permanent")
                        .addQueryParameter("scope", TextUtils.join(",", SCOPES))
                        .build();

                final Uri uri = Uri.parse(url.toString());

                client.warmup(0L);

                customTabsIntent.launchUrl(context, uri);
            }

            @Override
            public void onServiceDisconnected(final ComponentName name) {
            }
        };

        CustomTabsClient.bindCustomTabsService(context, CUSTOM_TAB_PACKAGE_NAME, connection);

        return state;
    }
}
