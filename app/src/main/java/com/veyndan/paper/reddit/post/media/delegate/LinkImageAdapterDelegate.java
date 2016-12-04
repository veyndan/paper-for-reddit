package com.veyndan.paper.reddit.post.media.delegate;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.jakewharton.rxbinding.view.RxView;
import com.veyndan.paper.reddit.databinding.PostMediaLinkImageBinding;
import com.veyndan.paper.reddit.post.media.model.LinkImage;
import com.veyndan.paper.reddit.post.model.Post;

import java.util.List;

import io.reactivex.Maybe;

public class LinkImageAdapterDelegate
        extends AbsListItemAdapterDelegate<LinkImage, Object, LinkImageAdapterDelegate.LinkImageViewHolder> {

    @NonNull private final Activity activity;
    @NonNull private final Maybe<CustomTabsClient> customTabsClient;
    @NonNull private final CustomTabsIntent customTabsIntent;
    @NonNull private final Post post;

    public LinkImageAdapterDelegate(@NonNull final Activity activity, @NonNull final Maybe<CustomTabsClient> customTabsClient,
                                    @NonNull final CustomTabsIntent customTabsIntent, @NonNull final Post post) {
        this.activity = activity;
        this.customTabsClient = customTabsClient;
        this.customTabsIntent = customTabsIntent;
        this.post = post;
    }

    @Override
    protected boolean isForViewType(@NonNull final Object item, @NonNull final List<Object> items,
                                    final int position) {
        return item instanceof LinkImage;
    }

    @NonNull
    @Override
    public LinkImageViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final PostMediaLinkImageBinding binding = PostMediaLinkImageBinding.inflate(inflater, parent, false);
        return new LinkImageViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull final LinkImage linkImage,
                                    @NonNull final LinkImageViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final Context context = holder.itemView.getContext();

        if (customTabsClient.count().blockingGet() == 1L) {
            final CustomTabsSession session = customTabsClient.blockingGet().newSession(null);
            session.mayLaunchUrl(Uri.parse(post.getLinkUrl()), null, null);
        }

        RxView.clicks(holder.itemView)
                .subscribe(aVoid -> {
                    customTabsIntent.launchUrl(activity, Uri.parse(post.getLinkUrl()));
                });

        holder.binding.postMediaImageProgress.setVisibility(View.VISIBLE);

        Glide.with(context)
                .load(linkImage.getUrl())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(@NonNull final Exception e, @NonNull final String model, @NonNull final Target<GlideDrawable> target, final boolean isFirstResource) {
                        holder.binding.postMediaImageProgress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull final GlideDrawable resource, @NonNull final String model, @NonNull final Target<GlideDrawable> target, final boolean isFromMemoryCache, final boolean isFirstResource) {
                        holder.binding.postMediaImageProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.binding.postMediaImage);

        holder.binding.postMediaUrl.setText(linkImage.getDomain());
    }

    static class LinkImageViewHolder extends RecyclerView.ViewHolder {

        @NonNull private final PostMediaLinkImageBinding binding;

        LinkImageViewHolder(@NonNull final PostMediaLinkImageBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
