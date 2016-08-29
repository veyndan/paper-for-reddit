package com.veyndan.redditclient.post;

import android.app.Activity;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates2.AdapterDelegatesManager;
import com.veyndan.redditclient.post.delegate.ImageAdapterDelegate;
import com.veyndan.redditclient.post.delegate.LinkAdapterDelegate;
import com.veyndan.redditclient.post.delegate.LinkImageAdapterDelegate;
import com.veyndan.redditclient.post.delegate.TweetAdapterDelegate;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

public class PostMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final AdapterDelegatesManager<List<Object>> delegatesManager;

    private final List<Object> items;

    public PostMediaAdapter(final Activity activity, final CustomTabsClient customTabsClient,
                            final CustomTabsIntent customTabsIntent, final Post post,
                            final int width, final List<Object> items) {
        this.items = items;

        delegatesManager = new AdapterDelegatesManager<>();

        // AdapterDelegatesManager internally assigns view types integers
        delegatesManager
                .addDelegate(new LinkAdapterDelegate(activity, customTabsClient, customTabsIntent, post))
                .addDelegate(new LinkImageAdapterDelegate(activity, customTabsClient, customTabsIntent, post))
                .addDelegate(new ImageAdapterDelegate(activity, customTabsClient, customTabsIntent, post, width))
                .addDelegate(new TweetAdapterDelegate());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return delegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        delegatesManager.onBindViewHolder(items, position, holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return delegatesManager.getItemViewType(items, position);
    }
}
