package com.veyndan.paper.reddit.post.media.mutator

import com.veyndan.paper.reddit.post.media.model.Image
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.util.regex.Matcher
import java.util.regex.Pattern

class ImgflipMutatorFactory : MutatorFactory {

    companion object {

        private val PATTERN: Pattern = Pattern.compile("^https?://(?:www\\.)?imgflip\\.com/i/(.*)$")
    }

    override fun mutate(post: Post): Maybe<Post> {
        val matcher: Matcher = PATTERN.matcher(post.linkUrl)

        return Single.just(post)
                .filter { matcher.matches() }
                .map {
                    val directImageUrl: String = "https://i.imgflip.com/${matcher.group(1)}.jpg"
                    val image: Image = Image(directImageUrl)
                    it.copy(it.medias.concatWith(Observable.just(image)))
                }
    }
}
