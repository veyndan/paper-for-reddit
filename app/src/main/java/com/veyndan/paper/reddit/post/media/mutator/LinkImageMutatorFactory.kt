package com.veyndan.paper.reddit.post.media.mutator

import com.veyndan.paper.reddit.api.reddit.model.Source
import com.veyndan.paper.reddit.post.media.model.LinkImage
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class LinkImageMutatorFactory : MutatorFactory {

    override fun mutate(post: Post): Maybe<Post> {
        return Single.just(post)
                .filter {it.preview.images.isNotEmpty() }
                .map {
                    val source: Source = it.preview.images[0].source
                    val linkImage: LinkImage = LinkImage(source.url, it.domain!!)
                    it.copy(it.medias.concatWith(Observable.just(linkImage)))
                }
    }
}
