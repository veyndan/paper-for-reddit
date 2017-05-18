package com.veyndan.paper.reddit.post.media.delegate

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsSession
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import com.jakewharton.rxbinding2.view.clicks
import com.veyndan.paper.reddit.databinding.PostMediaLinkImageBinding
import com.veyndan.paper.reddit.post.media.model.LinkImage
import com.veyndan.paper.reddit.post.model.Post
import java.lang.Exception

class LinkImageAdapterDelegate(private val activity: Activity,
                               private val customTabsClient: CustomTabsClient?,
                               private val customTabsIntent: CustomTabsIntent,
                               private val post: Post)
    : AbsListItemAdapterDelegate<LinkImage, Any, LinkImageAdapterDelegate.LinkImageViewHolder>() {

    override fun isForViewType(item: Any, items: List<Any>, position: Int): Boolean {
        return item is LinkImage
    }

    override fun onCreateViewHolder(parent: ViewGroup): LinkImageViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: PostMediaLinkImageBinding = PostMediaLinkImageBinding.inflate(inflater, parent, false)
        return LinkImageViewHolder(binding)
    }

    override fun onBindViewHolder(linkImage: LinkImage, holder: LinkImageViewHolder, payloads: List<Any>) {
        val context: Context = holder.itemView.context

        if (customTabsClient != null) {
            val session: CustomTabsSession = customTabsClient.newSession(null)
            session.mayLaunchUrl(Uri.parse(post.linkUrl), null, null)
        }

        holder.itemView.clicks()
                .subscribe {
                    customTabsIntent.launchUrl(activity, Uri.parse(post.linkUrl))
                }

        holder.binding.postMediaImageProgress.visibility = View.VISIBLE

        Glide.with(context)
                .load(linkImage.url)
                .listener(object: RequestListener<String, GlideDrawable> {
                    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        holder.binding.postMediaImageProgress.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        holder.binding.postMediaImageProgress.visibility = View.GONE
                        return false
                    }
                })
                .into(holder.binding.postMediaImage)

        holder.binding.postMediaUrl.text = linkImage.domain
    }

    class LinkImageViewHolder(val binding: PostMediaLinkImageBinding) : RecyclerView.ViewHolder(binding.root)
}
