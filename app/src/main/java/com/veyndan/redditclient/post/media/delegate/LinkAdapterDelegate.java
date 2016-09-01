package com.veyndan.redditclient.post.media.delegate;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates2.AbsListItemAdapterDelegate;
import com.jakewharton.rxbinding.view.RxView;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.post.media.model.Link;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkAdapterDelegate
        extends AbsListItemAdapterDelegate<Link, Object, LinkAdapterDelegate.LinkViewHolder> {

    private final Activity activity;
    private final CustomTabsClient customTabsClient;
    private final CustomTabsIntent customTabsIntent;
    private final Post post;

    public LinkAdapterDelegate(final Activity activity, final CustomTabsClient customTabsClient,
                               final CustomTabsIntent customTabsIntent, final Post post) {
        this.activity = activity;
        this.customTabsClient = customTabsClient;
        this.customTabsIntent = customTabsIntent;
        this.post = post;
    }

    @Override
    protected boolean isForViewType(@NonNull final Object item, final List<Object> items,
                                    final int position) {
        return item instanceof Link;
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.post_media_link, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Link link, @NonNull final LinkViewHolder holder) {
        if (customTabsClient != null) {
            final CustomTabsSession session = customTabsClient.newSession(null);

            session.mayLaunchUrl(Uri.parse(post.getLinkUrl()), null, null);
        }

        RxView.clicks(holder.urlView)
                .subscribe(aVoid -> {
                    customTabsIntent.launchUrl(activity, Uri.parse(post.getLinkUrl()));
                });

        holder.urlView.setText(link.getDomain());
    }

    // ButterKnife requires that binding occurs in non private classes.
    @SuppressWarnings("WeakerAccess")
    static class LinkViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_media_url) TextView urlView;

        public LinkViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
