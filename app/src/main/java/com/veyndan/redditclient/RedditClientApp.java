package com.veyndan.redditclient;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class RedditClientApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
