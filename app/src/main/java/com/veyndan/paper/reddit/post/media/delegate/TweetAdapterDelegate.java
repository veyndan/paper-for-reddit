package com.veyndan.paper.reddit.post.media.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;
import com.veyndan.paper.reddit.databinding.PostMediaTweetBinding;

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
        final PostMediaTweetBinding binding = PostMediaTweetBinding.inflate(inflater, parent, false);
        return new TweetViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Tweet tweet,
                                    @NonNull final TweetViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final Context context = holder.itemView.getContext();

        // Can't recycle TweetView as tweet must be specified on TweetView creation. Shit.
        // Could just create custom TweetView using the tweet object.
        holder.binding.tweetContainer.addView(new TweetView(context, tweet));
    }

    static class TweetViewHolder extends RecyclerView.ViewHolder {

        private final PostMediaTweetBinding binding;

        TweetViewHolder(final PostMediaTweetBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
