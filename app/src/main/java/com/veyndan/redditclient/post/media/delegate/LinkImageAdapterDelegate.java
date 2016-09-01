package com.veyndan.redditclient.post.media.delegate;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hannesdorfmann.adapterdelegates2.AbsListItemAdapterDelegate;
import com.jakewharton.rxbinding.view.RxView;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.post.media.model.LinkImage;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkImageAdapterDelegate
        extends AbsListItemAdapterDelegate<LinkImage, Object, LinkImageAdapterDelegate.LinkImageViewHolder> {

    private final Activity activity;
    private final CustomTabsClient customTabsClient;
    private final CustomTabsIntent customTabsIntent;
    private final Post post;

    public LinkImageAdapterDelegate(final Activity activity, final CustomTabsClient customTabsClient,
                                    final CustomTabsIntent customTabsIntent, final Post post) {
        this.activity = activity;
        this.customTabsClient = customTabsClient;
        this.customTabsIntent = customTabsIntent;
        this.post = post;
    }

    @Override
    protected boolean isForViewType(@NonNull final Object item, final List<Object> items,
                                    final int position) {
        return item instanceof LinkImage;
    }

    @NonNull
    @Override
    public LinkImageViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.post_media_link_image, parent, false);
        return new LinkImageViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final LinkImage linkImage,
                                    @NonNull final LinkImageViewHolder holder) {
        final Context context = holder.itemView.getContext();

        if (customTabsClient != null) {
            final CustomTabsSession session = customTabsClient.newSession(null);
            session.mayLaunchUrl(Uri.parse(post.getLinkUrl()), null, null);
        }

        RxView.clicks(holder.itemView)
                .subscribe(aVoid -> {
                    customTabsIntent.launchUrl(activity, Uri.parse(post.getLinkUrl()));
                });

        holder.imageProgressView.setVisibility(View.VISIBLE);

        Glide.with(context)
                .load(linkImage.getUrl())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(final Exception e, final String model, final Target<GlideDrawable> target, final boolean isFirstResource) {
                        holder.imageProgressView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final GlideDrawable resource, final String model, final Target<GlideDrawable> target, final boolean isFromMemoryCache, final boolean isFirstResource) {
                        holder.imageProgressView.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageView);

        holder.urlView.setText(linkImage.getDomain());
    }

    // ButterKnife requires that binding occurs in non private classes.
    @SuppressWarnings("WeakerAccess")
    static class LinkImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_media_image) ImageView imageView;
        @BindView(R.id.post_media_image_progress) ProgressBar imageProgressView;
        @BindView(R.id.post_media_url) TextView urlView;

        LinkImageViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
