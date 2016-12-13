package com.veyndan.paper.reddit.post.media.delegate;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.jakewharton.rxbinding.view.RxView;
import com.veyndan.paper.reddit.databinding.PostMediaImageBinding;
import com.veyndan.paper.reddit.post.media.model.Image;
import com.veyndan.paper.reddit.post.model.Post;

import java.util.List;

public class ImageAdapterDelegate
        extends AbsListItemAdapterDelegate<Image, Object, ImageAdapterDelegate.ImageViewHolder> {

    private final Activity activity;
    private final CustomTabsClient customTabsClient;
    private final CustomTabsIntent customTabsIntent;
    private final Post post;

    public ImageAdapterDelegate(final Activity activity, final CustomTabsClient customTabsClient,
                                final CustomTabsIntent customTabsIntent, final Post post) {
        this.activity = activity;
        this.customTabsClient = customTabsClient;
        this.customTabsIntent = customTabsIntent;
        this.post = post;
    }

    @Override
    protected boolean isForViewType(@NonNull final Object item, @NonNull final List<Object> items,
                                    final int position) {
        return item instanceof Image;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final PostMediaImageBinding binding = PostMediaImageBinding.inflate(inflater, parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Image image,
                                    @NonNull final ImageViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final Context context = holder.itemView.getContext();

        holder.binding.postMediaImageProgress.setVisibility(View.VISIBLE);

        if (customTabsClient != null) {
            final CustomTabsSession session = customTabsClient.newSession(null);
            session.mayLaunchUrl(Uri.parse(image.getUrl()), null, null);
        }

        RxView.clicks(holder.itemView)
                .subscribe(aVoid -> {
                    customTabsIntent.launchUrl(activity, Uri.parse(image.getUrl()));
                });

        final boolean imageDimensAvailable = image.getSize().getWidth() > 0 && image.getSize().getHeight() > 0;

        if (image.getType() == Image.IMAGE_TYPE_STANDARD) {
            holder.binding.postMediaImageType.setVisibility(View.GONE);
        } else {
            holder.binding.postMediaImageType.setVisibility(View.VISIBLE);
            holder.binding.postMediaImageType.setText(image.getType());
        }

        // TODO Once media adapter is shared between posts, width can be calculated in the holder constructor.
        RxView.layoutChanges(holder.itemView)
                .take(1)
                .subscribe(aVoid -> {
                    final int width = holder.itemView.getWidth();

                    Glide.with(context)
                            .load(image.getUrl())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(final Exception e, final String model, final Target<GlideDrawable> target, final boolean isFirstResource) {
                                    holder.binding.postMediaImageProgress.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(final GlideDrawable resource, final String model, final Target<GlideDrawable> target, final boolean isFromMemoryCache, final boolean isFirstResource) {
                                    holder.binding.postMediaImageProgress.setVisibility(View.GONE);
                                    if (!imageDimensAvailable) {
                                        final int imageWidth = resource.getIntrinsicWidth();
                                        final int imageHeight = resource.getIntrinsicHeight();

                                        image.setSize(new Size(imageWidth, imageHeight));

                                        post.getMedias().add(image);

                                        holder.binding.postMediaImage.getLayoutParams().height = (int) ((float) width / imageWidth * imageHeight);
                                    }
                                    return false;
                                }
                            })
                            .into(holder.binding.postMediaImage);

                    if (imageDimensAvailable) {
                        holder.binding.postMediaImage.getLayoutParams().height = (int) ((float) width / image.getSize().getWidth() * image.getSize().getHeight());
                    }
                });
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        private final PostMediaImageBinding binding;

        ImageViewHolder(final PostMediaImageBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
