package com.veyndan.paper.reddit.post.media.mutator

import com.veyndan.paper.reddit.api.reddit.model.PostHint
import com.veyndan.paper.reddit.api.xkcd.network.XkcdService
import com.veyndan.paper.reddit.post.media.model.Image
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.regex.Matcher
import java.util.regex.Pattern

class XkcdMutatorFactory : MutatorFactory {

    companion object {

        private val PATTERN: Pattern = Pattern.compile("^https?://(?:www\\.)?xkcd\\.com/(\\d+)/?$")
    }

    override fun mutate(post: Post): Maybe<Post> {
        val matcher: Matcher = PATTERN.matcher(post.linkUrl)

        return Single.just(post)
                .filter { matcher.matches() }
                .map {
                    val comicNum: Int = matcher.group(1).toInt()

                    val retrofit: Retrofit = Retrofit.Builder()
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(MoshiConverterFactory.create())
                            .baseUrl("https://xkcd.com")
                            .build()

                    val xkcdService: XkcdService = retrofit.create(XkcdService::class.java)

                    val image: Single<Image> = xkcdService.num(comicNum)
                            .map { comic -> Image(comic.body().img) }

                    it.copy(it.medias.concatWith(image.toObservable()), postHint = PostHint.IMAGE)
                }
    }
}
