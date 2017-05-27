package com.veyndan.paper.reddit.post.media.mutator

import android.support.annotation.StringRes
import android.util.Size
import com.veyndan.paper.reddit.api.reddit.model.PostHint
import com.veyndan.paper.reddit.api.reddit.model.Source
import com.veyndan.paper.reddit.post.media.model.Image
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class ImageMutatorFactory : MutatorFactory {

    override fun mutate(post: Post): Maybe<Post> {
        return Single.just(post)
                .filter { it.postHint == PostHint.IMAGE }
                .map {
                    val imageDimensAvailable: Boolean = it.preview.images.isNotEmpty()

                    val size = if (imageDimensAvailable) {
                        val source: Source = it.preview.images[0].source
                        Size(source.width, source.height)
                    } else {
                        Size(0, 0)
                    }

                    @StringRes val type: Int = if (it.linkUrl.endsWith(".gif")) Image.IMAGE_TYPE_GIF else Image.IMAGE_TYPE_STANDARD
                    val image: Image = Image(it.linkUrl, size, type)
                    it.copy(it.medias.concatWith(Observable.just(image)))
                }
    }
}
