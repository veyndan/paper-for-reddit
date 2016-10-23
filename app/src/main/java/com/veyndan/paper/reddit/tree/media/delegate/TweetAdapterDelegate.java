package com.veyndan.paper.reddit.tree.media.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;
import com.veyndan.paper.reddit.R;

import java.util.List;

public class TweetAdapterDelegate
        extends AbsListItemAdapterDelegate<Tweet, Object, TweetAdapterDelegate.TweetViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Object item, @NonNull final List<Object> items,
                                    final int position) {
        return item instanceof Tweet;
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.post_media_tweet, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Tweet tweet,
                                    @NonNull final TweetViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final Context context = holder.itemView.getContext();

        // Can't recycle TweetView as tweet must be specified on TweetView creation. Shit.
        // Could just create custom TweetView using the tweet object.
        holder.container.addView(new TweetView(context, tweet));
    }

    static class TweetViewHolder extends RecyclerView.ViewHolder {

        final ViewGroup container;

        TweetViewHolder(final View itemView) {
            super(itemView);

            container = (ViewGroup) itemView;
        }
    }
}
