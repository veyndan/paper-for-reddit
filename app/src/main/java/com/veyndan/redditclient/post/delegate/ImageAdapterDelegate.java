package com.veyndan.redditclient.post.delegate;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hannesdorfmann.adapterdelegates2.AdapterDelegate;
import com.jakewharton.rxbinding.view.RxView;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Image;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class ImageAdapterDelegate implements AdapterDelegate<List<Object>> {

    private final Activity activity;
    private final CustomTabsClient customTabsClient;
    private final CustomTabsIntent customTabsIntent;
    private final Post post;
    private final int width;

    public ImageAdapterDelegate(final Activity activity, final CustomTabsClient customTabsClient,
                                final CustomTabsIntent customTabsIntent, final Post post,
                                final int width) {
        this.activity = activity;
        this.customTabsClient = customTabsClient;
        this.customTabsIntent = customTabsIntent;
        this.post = post;
        this.width = width;
    }

    @Override
    public boolean isForViewType(@NonNull final List<Object> items, final int position) {
        return items.get(position) instanceof Image;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.post_media_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List<Object> items, final int position,
                                 @NonNull final RecyclerView.ViewHolder holder) {
        final Context context = holder.itemView.getContext();

        final ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
        final Image image = (Image) items.get(position);

        imageViewHolder.imageProgressView.setVisibility(View.VISIBLE);

        if (customTabsClient != null) {
            final CustomTabsSession session = customTabsClient.newSession(null);
            session.mayLaunchUrl(Uri.parse(image.getUrl()), null, null);
        }

        RxView.clicks(imageViewHolder.itemView)
                .subscribe(aVoid -> {
                    customTabsIntent.launchUrl(activity, Uri.parse(image.getUrl()));
                });

        final boolean imageDimensAvailable = image.getWidth() > 0 && image.getHeight() > 0;

        Glide.with(context)
                .load(image.getUrl())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(final Exception e, final String model, final Target<GlideDrawable> target, final boolean isFirstResource) {
                        imageViewHolder.imageProgressView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final GlideDrawable resource, final String model, final Target<GlideDrawable> target, final boolean isFromMemoryCache, final boolean isFirstResource) {
                        imageViewHolder.imageProgressView.setVisibility(View.GONE);
                        if (!imageDimensAvailable) {
                            final int imageWidth = resource.getIntrinsicWidth();
                            final int imageHeight = resource.getIntrinsicHeight();

                            image.setWidth(imageWidth);
                            image.setHeight(imageHeight);

                            post.setMediaObservable(Observable.just(image));

                            imageViewHolder.imageView.getLayoutParams().height = (int) ((float) width / imageWidth * imageHeight);
                        }
                        return false;
                    }
                })
                .into(imageViewHolder.imageView);

        if (imageDimensAvailable) {
            imageViewHolder.imageView.getLayoutParams().height = (int) ((float) width / image.getWidth() * image.getHeight());
        }
    }

    // ButterKnife requires that binding occurs in non private classes.
    @SuppressWarnings("WeakerAccess")
    static class ImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_media_image) ImageView imageView;
        @BindView(R.id.post_media_image_progress) ProgressBar imageProgressView;

        ImageViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
