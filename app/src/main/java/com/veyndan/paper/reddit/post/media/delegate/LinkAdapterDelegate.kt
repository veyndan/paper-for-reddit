package com.veyndan.paper.reddit.post.media.delegate

import android.app.Activity
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsSession
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import com.jakewharton.rxbinding2.view.clicks
import com.veyndan.paper.reddit.databinding.PostMediaLinkBinding
import com.veyndan.paper.reddit.post.media.model.Link
import com.veyndan.paper.reddit.post.model.Post

class LinkAdapterDelegate(private val activity: Activity,
                          private val customTabsClient: CustomTabsClient?,
                          private val customTabsIntent: CustomTabsIntent,
                          private val post: Post)
    : AbsListItemAdapterDelegate<Link, Any, LinkAdapterDelegate.LinkViewHolder>() {

    override fun isForViewType(item: Any, items: List<Any>, position: Int): Boolean {
        return item is Link
    }

    override fun onCreateViewHolder(parent: ViewGroup): LinkViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: PostMediaLinkBinding = PostMediaLinkBinding.inflate(inflater, parent, false)
        return LinkViewHolder(binding)
    }

    override fun onBindViewHolder(link: Link, holder: LinkViewHolder, payloads: List<Any>) {
        if (customTabsClient != null) {
            val session: CustomTabsSession = customTabsClient.newSession(null)
            session.mayLaunchUrl(Uri.parse(post.linkUrl), null, null)
        }

        holder.binding.postMediaUrl.clicks()
                .subscribe {
                    customTabsIntent.launchUrl(activity, Uri.parse(post.linkUrl))
                }

        holder.binding.postMediaUrl.text = link.domain
    }

    class LinkViewHolder(val binding: PostMediaLinkBinding) : RecyclerView.ViewHolder(binding.root)
}
