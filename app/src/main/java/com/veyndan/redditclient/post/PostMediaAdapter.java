package com.veyndan.redditclient.post;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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
import com.jakewharton.rxbinding.view.RxView;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Image;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class PostMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_TWEET = 1;

    private final Activity activity;
    private final CustomTabsClient customTabsClient;
    private final CustomTabsIntent customTabsIntent;
    private final Post post;
    private final int width;
    private final List<Image> images;
    private final List<Tweet> tweets;

    public PostMediaAdapter(final Activity activity,
                            final CustomTabsClient customTabsClient,
                            final CustomTabsIntent customTabsIntent,
                            final Post post,
                            final int width,
                            final List<Image> images, final List<Tweet> tweets) {
        this.activity = activity;
        this.customTabsClient = customTabsClient;
        this.customTabsIntent = customTabsIntent;
        this.post = post;
        this.width = width;
        this.images = images;
        this.tweets = tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_IMAGE:
                View view = inflater.inflate(R.layout.post_media_image, parent, false);
                return new ImageViewHolder(view);
            case TYPE_TWEET:
                view = inflater.inflate(R.layout.post_media_tweet, parent, false);
                return new TweetViewHolder(view);
            default:
                throw new IllegalStateException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Context context = holder.itemView.getContext();

        switch (holder.getItemViewType()) {
            case TYPE_IMAGE:
                final ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                final Image image = images.get(position);

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

                                    post.setImageObservable(Observable.just(image));

                                    imageViewHolder.imageView.getLayoutParams().height = (int) ((float) width / imageWidth * imageHeight);
                                }
                                return false;
                            }
                        })
                        .into(imageViewHolder.imageView);

                if (imageDimensAvailable) {
                    imageViewHolder.imageView.getLayoutParams().height = (int) ((float) width / image.getWidth() * image.getHeight());
                }
                break;
            case TYPE_TWEET:
                final TweetViewHolder tweetViewHolder = (TweetViewHolder) holder;
                final Tweet tweet = tweets.get(position);

                // Can't recycle TweetView as tweet must be specified on TweetView creation. Shit.
                // Could just create custom TweetView using the tweet object.
                tweetViewHolder.container.addView(new TweetView(context, tweet));
                break;
            default:
                throw new IllegalStateException("Unknown view type: " + holder.getItemViewType());
        }
    }

    @Override
    public int getItemCount() {
        return images.size() + tweets.size();
    }

    @Override
    public int getItemViewType(final int position) {
        // TODO Can't mix types like this e.g. an image and tweet won't go together as TYPE_IMAGE
        // will be returned for both the image and tweet.
        if (images.size() > 0) return TYPE_IMAGE;
        else if (tweets.size() > 0) return TYPE_TWEET;
        else throw new IllegalStateException("Can't retrieve view type");
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

    private static class TweetViewHolder extends RecyclerView.ViewHolder {

        final ViewGroup container;

        TweetViewHolder(final View itemView) {
            super(itemView);

            container = (ViewGroup) itemView;
        }
    }
}
