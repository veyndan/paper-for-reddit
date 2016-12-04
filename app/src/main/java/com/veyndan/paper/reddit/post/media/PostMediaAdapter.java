package com.veyndan.paper.reddit.post.media;

import android.app.Activity;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.veyndan.paper.reddit.post.media.delegate.ImageAdapterDelegate;
import com.veyndan.paper.reddit.post.media.delegate.LinkAdapterDelegate;
import com.veyndan.paper.reddit.post.media.delegate.LinkImageAdapterDelegate;
import com.veyndan.paper.reddit.post.media.delegate.TextAdapterDelegate;
import com.veyndan.paper.reddit.post.media.delegate.TweetAdapterDelegate;
import com.veyndan.paper.reddit.post.model.Post;

import java.util.List;

import io.reactivex.Maybe;

public class PostMediaAdapter extends ListDelegationAdapter<List<Object>> {

    public PostMediaAdapter(final Activity activity, final Maybe<CustomTabsClient> customTabsClient,
                            final CustomTabsIntent customTabsIntent, final Post post,
                            final List<Object> items) {
        delegatesManager
                .addDelegate(new TextAdapterDelegate())
                .addDelegate(new LinkAdapterDelegate(activity, customTabsClient, customTabsIntent, post))
                .addDelegate(new LinkImageAdapterDelegate(activity, customTabsClient, customTabsIntent, post))
                .addDelegate(new ImageAdapterDelegate(activity, customTabsClient, customTabsIntent, post))
                .addDelegate(new TweetAdapterDelegate());

        setItems(items);
    }
}
