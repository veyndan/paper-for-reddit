package com.veyndan.paper.reddit.post.media.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.TweetView
import com.veyndan.paper.reddit.databinding.PostMediaTweetBinding

class TweetAdapterDelegate
    : AbsListItemAdapterDelegate<Tweet, Any, TweetAdapterDelegate.TweetViewHolder>() {

    override fun isForViewType(item: Any, items: List<Any>, position: Int): Boolean {
        return item is Tweet
    }

    override fun onCreateViewHolder(parent: ViewGroup): TweetViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: PostMediaTweetBinding = PostMediaTweetBinding.inflate(inflater, parent, false)
        return TweetViewHolder(binding)
    }

    override fun onBindViewHolder(tweet: Tweet, holder: TweetViewHolder, payloads: List<Any>) {
        val context: Context = holder.itemView.context

        // Can't recycle TweetView as tweet must be specified on TweetView creation. Shit.
        // Could just create custom TweetView using the tweet object.
        holder.binding.tweetContainer.addView(TweetView(context, tweet))
    }

    class TweetViewHolder(val binding: PostMediaTweetBinding) : RecyclerView.ViewHolder(binding.root)
}
