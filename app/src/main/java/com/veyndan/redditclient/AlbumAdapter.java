package com.veyndan.redditclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private static final String TAG = "veyndan_AlbumAdapter";

    private final List<Image> images;
    private final int width;

    public AlbumAdapter(List<Image> images, int width) {
        this.images = images;
        this.width = width;
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

        holder.mediaContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Image image = images.get(holder.getAdapterPosition());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(image.link));
                context.startActivity(intent);
            }
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
