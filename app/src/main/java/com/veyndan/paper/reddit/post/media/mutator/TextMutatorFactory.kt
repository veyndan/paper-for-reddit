package com.veyndan.paper.reddit.post.media.mutator

import android.content.Context
import android.text.Spannable
import com.veyndan.paper.reddit.api.reddit.model.PostHint
import com.veyndan.paper.reddit.post.media.model.Text
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function

class TextMutatorFactory : MutatorFactory {

    override fun mutate(post: Post): Maybe<Post> {
        return Single.just(post)
                .filter { it.postHint == PostHint.SELF && !it.bodyHtml.isNullOrEmpty() }
                .map {
                    val text: Text = Text(Function<Context, Spannable>(it::getDisplayBody))
                    it.copy(it.medias.concatWith(Observable.just(text)))
                }
    }
}
