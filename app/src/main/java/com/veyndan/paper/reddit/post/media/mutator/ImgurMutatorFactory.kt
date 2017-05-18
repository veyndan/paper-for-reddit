package com.veyndan.paper.reddit.post.media.mutator

import android.support.annotation.StringRes
import android.util.Size
import com.veyndan.paper.reddit.BuildConfig
import com.veyndan.paper.reddit.api.imgur.network.ImgurService
import com.veyndan.paper.reddit.api.reddit.model.PostHint
import com.veyndan.paper.reddit.api.reddit.model.Source
import com.veyndan.paper.reddit.post.media.model.Image
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.regex.Matcher
import java.util.regex.Pattern

class ImgurMutatorFactory : MutatorFactory {

    companion object {

        val PATTERN: Pattern = Pattern.compile("^https?://(?:m\\.|www\\.)?(i\\.)?imgur\\.com/(a/|gallery/)?(.*)$")
    }

    override fun mutate(post: Post): Maybe<Post> {
        val matcher: Matcher = PATTERN.matcher(post.linkUrl)

        return Single.just(post)
                .filter {BuildConfig.HAS_IMGUR_API_CREDENTIALS && matcher.matches()}
                .map {
                    val isAlbum: Boolean = matcher.group(2) != null
                    val isDirectImage: Boolean = matcher.group(1) != null

                    var linkUrl: String = it.linkUrl
                    var postHint: PostHint = it.postHint

                    if (!isAlbum && !isDirectImage) {
                        // TODO .gifv links are HTML 5 videos so the PostHint should be set accordingly.
                        if (!linkUrl.endsWith(".gifv")) {
                            linkUrl = singleImageUrlToDirectImageUrl(linkUrl)
                            postHint = PostHint.IMAGE
                        }
                    }

                    val images: Observable<Image>

                    if (isAlbum) {
                        postHint = PostHint.IMAGE

                        val client: OkHttpClient = OkHttpClient.Builder()
                                .addInterceptor { chain ->
                                    val request: Request = chain.request().newBuilder()
                                            .addHeader("Authorization", "Client-ID ${BuildConfig.IMGUR_API_KEY}")
                                            .build()
                                    chain.proceed(request)
                                }
                                .build()

                        val retrofit: Retrofit = Retrofit.Builder()
                                .baseUrl("https://api.imgur.com/3/")
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .addConverterFactory(MoshiConverterFactory.create())
                                .client(client)
                                .build()

                        val imgurService: ImgurService = retrofit.create(ImgurService::class.java)

                        val id: String = matcher.group(3)

                        images = imgurService.album(id)
                                .flattenAsObservable { basic -> basic.body().data.images }
                                .map { (width, height, link) -> Image(link, Size(width, height)) }
                    } else {
                        val imageDimensAvailable: Boolean = it.preview.images.isNotEmpty()

                        val url: String = if (linkUrl.endsWith(".gifv") && imageDimensAvailable) it.preview.images[0].source.url else linkUrl

                        val size: Size
                        if (imageDimensAvailable) {
                            val source: Source = it.preview.images[0].source
                            size = Size(source.width, source.height)
                        } else {
                            size = Size(0, 0)
                        }

                        @StringRes val type: Int = if (linkUrl.endsWith(".gif") || linkUrl.endsWith(".gifv")) Image.IMAGE_TYPE_GIF else Image.IMAGE_TYPE_STANDARD

                        images = Observable.just(Image(url, size, type))
                    }

                    it.copy(it.medias.concatWith(images), linkUrl = linkUrl, postHint = postHint)
                }
    }

    /**
     * Returns a direct image url
     * (e.g. <a href="http://i.imgur.com/1AGVxLl.png">http://i.imgur.com/1AGVxLl.png</a>) from a
     * single image url (e.g. <a href="http://imgur.com/1AGVxLl">http://imgur.com/1AGVxLl</a>)
     *
     * @param url The single image url.
     * @return The direct image url.
     */
    private fun singleImageUrlToDirectImageUrl(url: String): String {
        return "${HttpUrl.parse(url).newBuilder().host("i.imgur.com").build()}.png"
    }
}
