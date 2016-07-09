package com.veyndan.redditclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private static final String TAG = "veyndan_AlbumAdapter";

    private final List<Image> images;

    public AlbumAdapter(List<Image> images) {
        this.images = images;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_media_album_image, parent, false);
        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(images.get(position).link)
                .into((ImageView) holder.itemView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        public AlbumViewHolder(View itemView) {
            super(itemView);
        }
    }
}
