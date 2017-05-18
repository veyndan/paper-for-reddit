package com.veyndan.paper.reddit.post.media.mutator

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.TweetUtils
import com.veyndan.paper.reddit.BuildConfig
import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class TwitterMutatorFactory : MutatorFactory {

    companion object {

        private val PATTERN: Pattern = Pattern.compile("^https?://(?:www\\.)?twitter\\.com/\\w*/status/(\\d+)\\??.*$")
    }

    override fun mutate(post: Post): Maybe<Post> {
        val matcher: Matcher = PATTERN.matcher(post.linkUrl)

        return Single.just(post)
                .filter { BuildConfig.HAS_TWITTER_API_CREDENTIALS && matcher.matches() }
                .map {
                    val tweetId: Long = matcher.group(1).toLong()
                    // TODO Replace Observable.create with an Observable returned by Retrofit.
                    val tweet: Single<Response<Tweet>> = Single.create { subscriber ->
                        TweetUtils.loadTweet(tweetId, object : Callback<Tweet>() {
                            override fun success(result: Result<Tweet>?) {
                                subscriber.onSuccess(Response.success(result!!.data))
                            }

                            override fun failure(exception: TwitterException?) {
                                subscriber.onSuccess(Response.error(404, ResponseBody.create(MediaType.parse("application/json"), "{}")))
                            }
                        })
                    }
                    it.copy(it.medias.concatWith(tweet.map { it.body() }.toObservable()))
                }
    }
}
