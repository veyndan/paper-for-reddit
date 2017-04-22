package com.veyndan.paper.reddit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.veyndan.paper.reddit.api.reddit.network.Credentials;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class PaperForRedditApp extends Application {

    // TODO These should be defined somewhere else which is more descriptive e.g. ServiceConfig.java? Maybe put TwitterAuthConfig there as well in future commit?
    private static final String REDDIT_USER_AGENT = Credentials.createUserAgent("android",
            BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.REDDIT_USER_NAME);
    public static final Credentials REDDIT_CREDENTIALS = Credentials.create(
            BuildConfig.REDDIT_API_KEY, BuildConfig.REDDIT_API_SECRET, REDDIT_USER_AGENT,
            BuildConfig.REDDIT_USER_NAME, BuildConfig.REDDIT_USER_PASSWORD);

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected void log(final int priority, final String tag, final String message,
                                   final Throwable t) {
                    super.log(priority, "veyndan_" + tag, message, t);
                }
            });
        }
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
