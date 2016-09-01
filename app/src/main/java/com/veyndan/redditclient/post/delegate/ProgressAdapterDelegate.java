package com.veyndan.redditclient.post.delegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hannesdorfmann.adapterdelegates2.AdapterDelegate;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

public class ProgressAdapterDelegate implements AdapterDelegate<List<Post>> {

    @Override
    public boolean isForViewType(@NonNull final List<Post> posts, final int position) {
        return position == posts.size() - 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View progressView = inflater.inflate(R.layout.progress_item, parent, false);
        return new ProgressViewHolder(progressView);
    }

    @Override
    public void onBindViewHolder(@NonNull final List<Post> posts, final int position,
                                 @NonNull final RecyclerView.ViewHolder holder) {
    }

    private final static class ProgressViewHolder extends RecyclerView.ViewHolder {

        final ProgressBar progress;

        ProgressViewHolder(final View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView;
        }
    }
}
