package com.veyndan.paper.reddit.post.media

import android.app.Activity
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.veyndan.paper.reddit.post.media.delegate.*
import com.veyndan.paper.reddit.post.model.Post

class PostMediaAdapter(activity: Activity, customTabsClient: CustomTabsClient?,
                       customTabsIntent: CustomTabsIntent, post: Post, medias: MutableList<Any>)
    : ListDelegationAdapter<MutableList<Any>>() {

    init {
        delegatesManager
                .addDelegate(TextAdapterDelegate())
                .addDelegate(LinkAdapterDelegate(activity, customTabsClient, customTabsIntent, post))
                .addDelegate(LinkImageAdapterDelegate(activity, customTabsClient, customTabsIntent, post))
                .addDelegate(ImageAdapterDelegate(activity, customTabsClient, customTabsIntent, post))
                .addDelegate(TweetAdapterDelegate())

        setItems(medias)
    }
}
