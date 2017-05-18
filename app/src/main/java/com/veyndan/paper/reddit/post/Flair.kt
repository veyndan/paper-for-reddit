package com.veyndan.paper.reddit.post

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import com.veyndan.paper.reddit.R

sealed class Flair(@ColorInt val backgroundColor: Int, val text: String?,
                   val icon: Drawable? = null, val searchQuery: String? = null) {

    init {
        check(backgroundColor != 0) { "backgroundColor must be set" }
    }

    fun searchable(): Boolean {
        return searchQuery != null
    }

    data class Stickied(val context: Context) : Flair(
            ContextCompat.getColor(context, R.color.post_flair_locked),
            context.getString(R.string.post_locked),
            ContextCompat.getDrawable(context, R.drawable.ic_lock_outline_white_12sp))

    data class Locked(val context: Context) : Flair(
            ContextCompat.getColor(context, R.color.post_flair_locked),
            context.getString(R.string.post_locked),
            ContextCompat.getDrawable(context, R.drawable.ic_lock_outline_white_12sp))

    data class Nsfw(val context: Context) : Flair(
            ContextCompat.getColor(context, R.color.post_flair_nsfw),
            context.getString(R.string.post_nsfw),
            searchQuery = "nsfw:yes")

    data class Link(val context: Context, val text1: String?) : Flair(
            ContextCompat.getColor(context, R.color.post_flair_link),
            text1,
            searchQuery = String.format("flair:'%s'", text1))

    data class Gilded(val context: Context, val gildedCount: Int) : Flair(
            ContextCompat.getColor(context, R.color.post_flair_gilded),
            gildedCount.toString(),
            ContextCompat.getDrawable(context, R.drawable.ic_star_white_12sp))
}
