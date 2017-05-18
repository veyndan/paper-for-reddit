package com.veyndan.paper.reddit

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex

import com.squareup.leakcanary.LeakCanary
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.veyndan.paper.reddit.api.reddit.network.Credentials

import io.fabric.sdk.android.Fabric
import timber.log.Timber

class PaperForRedditApp : Application() {

    companion object {

        // TODO These should be defined somewhere else which is more descriptive e.g. ServiceConfig.java? Maybe put TwitterAuthConfig there as well in future commit?
        private val REDDIT_USER_AGENT = Credentials.createUserAgent("android",
                BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.REDDIT_USER_NAME)
        val REDDIT_CREDENTIALS = Credentials(
                BuildConfig.REDDIT_API_KEY, BuildConfig.REDDIT_API_SECRET, REDDIT_USER_AGENT,
                BuildConfig.REDDIT_USER_NAME, BuildConfig.REDDIT_USER_PASSWORD)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String,
                                 t: Throwable?) {
                    super.log(priority, "veyndan_$tag", message, t)
                }
            })
        }
        val authConfig = TwitterAuthConfig(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET)
        Fabric.with(this, Twitter(authConfig))
        LeakCanary.install(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
