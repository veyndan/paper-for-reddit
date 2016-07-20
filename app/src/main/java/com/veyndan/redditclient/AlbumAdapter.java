package com.veyndan.redditclient;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private final Activity activity;
    private final List<Image> images;
    private final int width;
    private final CustomTabsClient customTabsClient;
    private final CustomTabsIntent customTabsIntent;

    public AlbumAdapter(Activity activity, List<Image> images, int width, @Nullable CustomTabsClient customTabsClient, CustomTabsIntent customTabsIntent) {
        this.activity = activity;
        this.images = images;
        this.width = width;
        this.customTabsClient = customTabsClient;
        this.customTabsIntent = customTabsIntent;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_media_image, parent, false);
        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder holder, int position) {
        final Image image = images.get(position);
        final Context context = holder.itemView.getContext();

        holder.mediaImageProgress.setVisibility(View.VISIBLE);

        if (customTabsClient != null) {
            CustomTabsSession session = customTabsClient.newSession(null);
            session.mayLaunchUrl(Uri.parse(image.link), null, null);
        }

        RxView.clicks(holder.mediaImageProgress)
                .subscribe(aVoid -> {
                    customTabsIntent.launchUrl(activity, Uri.parse(image.link));
                });

        Glide.with(context)
                .load(image.link)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.mediaImageProgress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.mediaImageProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.mediaImage);
        holder.mediaImage.getLayoutParams().height = (int) ((float) width / image.width * image.height);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        View mediaContainer;
        @BindView(R.id.post_media_image) ImageView mediaImage;
        @BindView(R.id.post_media_image_progress) ProgressBar mediaImageProgress;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mediaContainer = itemView;
        }
    }
}
