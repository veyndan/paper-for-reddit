package com.veyndan.redditclient.post.media.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates2.AdapterDelegate;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;
import com.veyndan.redditclient.R;

import java.util.List;

public class TweetAdapterDelegate implements AdapterDelegate<List<Object>> {

    @Override
    public boolean isForViewType(@NonNull final List<Object> items, final int position) {
        return items.get(position) instanceof Tweet;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.post_media_tweet, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List<Object> items, final int position,
                                 @NonNull final RecyclerView.ViewHolder holder) {
        final Context context = holder.itemView.getContext();

        final TweetViewHolder tweetViewHolder = (TweetViewHolder) holder;
        final Tweet tweet = (Tweet) items.get(position);

        // Can't recycle TweetView as tweet must be specified on TweetView creation. Shit.
        // Could just create custom TweetView using the tweet object.
        tweetViewHolder.container.addView(new TweetView(context, tweet));
    }

    private static class TweetViewHolder extends RecyclerView.ViewHolder {

        final ViewGroup container;

        TweetViewHolder(final View itemView) {
            super(itemView);

            container = (ViewGroup) itemView;
        }
    }
}
