package com.veyndan.paper.reddit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class PaperForRedditApp extends Application {

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
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(Config.TWITTER_KEY, Config.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
