package com.veyndan.paper.reddit.post.media.mutator

import com.veyndan.paper.reddit.api.reddit.model.PostHint
import com.veyndan.paper.reddit.post.media.model.Link
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class LinkMutatorFactory : MutatorFactory {

    override fun mutate(post: Post): Maybe<Post> {
        return Single.just(post)
                .filter { it.postHint != PostHint.SELF }
                .map {
                    val link: Link = Link(it.domain!!)
                    it.copy(it.medias.concatWith(Observable.just(link)))
                }
    }
}
