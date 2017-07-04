package com.veyndan.paper.reddit.post.media.mutator

import com.veyndan.paper.reddit.api.reddit.model.PostHint
import com.veyndan.paper.reddit.post.media.model.Text
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class TextMutatorFactory : MutatorFactory {

    override fun mutate(post: Post): Maybe<Post> {
        return Single.just(post)
                .filter { it.postHint == PostHint.SELF && !it.bodyHtml.isNullOrEmpty() }
                .map { post1 ->
                    val text: Text = Text { context -> post1.getDisplayBody(context)!! }
                    post1.copy(post1.medias.concatWith(Observable.just(text)))
                }
    }
}
