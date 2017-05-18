package com.veyndan.paper.reddit.post.media.model

import android.content.Context
import android.text.Spannable
import io.reactivex.functions.Function

data class Text(val body: Function<Context, Spannable>) {

    fun getBody(context: Context): Spannable {
        try {
            return body.apply(context)
        } catch (e: Exception) {
            throw IllegalStateException()
        }
    }
}
