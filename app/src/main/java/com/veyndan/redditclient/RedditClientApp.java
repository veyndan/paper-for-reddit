package com.veyndan.redditclient;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class RedditClientApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Config.TWITTER_KEY, Config.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        LeakCanary.install(this);
    }
}
