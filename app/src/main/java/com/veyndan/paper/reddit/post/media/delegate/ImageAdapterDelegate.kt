package com.veyndan.paper.reddit.post.media.delegate

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsSession
import android.support.v7.widget.RecyclerView
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.layoutChanges
import com.veyndan.paper.reddit.databinding.PostMediaImageBinding
import com.veyndan.paper.reddit.post.media.model.Image
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Observable

class ImageAdapterDelegate(private val activity: Activity,
                           private val customTabsClient: CustomTabsClient?,
                           private val customTabsIntent: CustomTabsIntent,
                           private val post: Post)
    : AbsListItemAdapterDelegate<Image, Any, ImageAdapterDelegate.ImageViewHolder>() {

    override fun isForViewType(item: Any, items: List<Any>, position: Int): Boolean {
        return item is Image
    }

    override fun onCreateViewHolder(parent: ViewGroup): ImageViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: PostMediaImageBinding = PostMediaImageBinding.inflate(inflater, parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(image: Image, holder: ImageViewHolder, payloads: List<Any>) {
        val context: Context = holder.itemView.context

        holder.binding.postMediaImageProgress.visibility = View.VISIBLE

        if (customTabsClient != null) {
            val session: CustomTabsSession = customTabsClient.newSession(null)
            session.mayLaunchUrl(Uri.parse(image.url), null, null)
        }

        holder.itemView.clicks()
                .subscribe{
                    customTabsIntent.launchUrl(activity, Uri.parse(image.url))
                }

        val imageDimensAvailable : Boolean = image.size.width > 0 && image.size.height > 0

        if (image.type == Image.IMAGE_TYPE_STANDARD) {
            holder.binding.postMediaImageType.visibility = View.GONE
        } else {
            holder.binding.postMediaImageType.visibility = View.VISIBLE
            holder.binding.postMediaImageType.setText(image.type)
        }

        // TODO Once media adapter is shared between posts, width can be calculated in the holder constructor.
        holder.itemView.layoutChanges()
                .take(1)
                .subscribe {
            val width: Int = holder.itemView.width

            Glide.with(context)
                    .load(image.url)
                    .listener(object: RequestListener<Drawable> {
                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            holder.binding.postMediaImageProgress.visibility = View.GONE
                            if (!imageDimensAvailable) {
                                val imageWidth : Int = resource!!.intrinsicWidth
                                val imageHeight : Int = resource.intrinsicHeight

                                image.size = Size(imageWidth, imageHeight)

                                post.medias = post.medias.concatWith(Observable.just(image))

                                holder.binding.postMediaImage.layoutParams.height = (width.toFloat() / imageWidth * imageHeight).toInt()
                            }
                            return false
                        }

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            holder.binding.postMediaImageProgress.visibility = View.GONE
                            return false
                        }
                    })
                    .into(holder.binding.postMediaImage)

            if (imageDimensAvailable) {
                holder.binding.postMediaImage.layoutParams.height = (width.toFloat() / image.size.width * image.size.height).toInt()
            }
        }
    }

    class ImageViewHolder(val binding: PostMediaImageBinding) : RecyclerView.ViewHolder(binding.root)
}
