package com.veyndan.paper.reddit.post.media.mutator

import com.veyndan.paper.reddit.post.media.model.Image
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class ImgflipMutatorFactory : MutatorFactory {

    companion object {

        private val REGEX = Regex("^https?://(?:www\\.)?imgflip\\.com/i/(.*)#.*$")
    }

    override fun mutate(post: Post): Maybe<Post> {
        val matchResult = REGEX.matchEntire(post.linkUrl)

        return Single.just(post)
                .filter { matchResult != null }
                .map {
                    val directImageUrl: String = "https://i.imgflip.com/${matchResult!!.groupValues[1]}.jpg"
                    val image: Image = Image(directImageUrl)
                    it.copy(it.medias.concatWith(Observable.just(image)))
                }
    }
}
