package com.veyndan.paper.reddit.post.model

import android.content.Context
import android.content.res.Resources
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import com.veyndan.paper.reddit.R
import com.veyndan.paper.reddit.api.reddit.model.*
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection
import com.veyndan.paper.reddit.post.Flair
import com.veyndan.paper.reddit.post.media.mutator.Mutators
import com.veyndan.paper.reddit.util.Linkifier
import com.veyndan.paper.reddit.util.Node
import io.reactivex.Observable
import io.reactivex.Single
import org.apache.commons.lang3.StringEscapeUtils
import retrofit2.Response
import java.util.concurrent.TimeUnit

/**
 * @param linkUrl Returns the url of the link and the empty string if there is no link url e.g.
 *                for a comment.
 */
data class Post(var medias: Observable<Any> = Observable.empty(), val comment: Boolean,
                val children: Observable<Node<Response<Thing<Listing>>>>, val commentCount: Int?,
                var descendantsVisible: Boolean, val archived: Boolean, val author: String,
                val bodyHtml: String?, val createdUtc: Long, val domain: String?,
                val fullname: String, val gildedCount: Int, val hideable: Boolean,
                var likes: VoteDirection, val linkFlair: String?, val linkTitle: String?,
                val linkUrl: String, val locked: Boolean, val nsfw: Boolean, val permalink: String,
                var points: Int, val postHint: PostHint, val preview: Preview, var saved: Boolean,
                val scoreHidden: Boolean, val stickied: Boolean, val subreddit: String)
    : Node<Response<Thing<Listing>>>() {

    companion object {

        @JvmStatic
        fun create(submission: Submission): Post {
            val comment: Boolean = submission is Comment

            val children: Observable<Node<Response<Thing<Listing>>>> = Observable.fromIterable(submission.replies.data.children)
                    .flatMapSingle { redditObject ->
                        when (redditObject) {
                            is Submission -> Single.just(redditObject)
                                    .cast(Submission::class.java)
                                    .map { Post.create(it) }
                                    .flatMap(Mutators.mutate())
                            is More -> Single.just(redditObject)
                                    .cast(More::class.java)
                                    .map { Progress(it.count) }
                            else -> {
                                val message: String = "Unknown node class: $redditObject"
                                Single.error(IllegalStateException(message))
                            }
                        }
                    }

            return Post(
                    comment = comment,
                    children = children,
                    commentCount = submission.numComments,
                    descendantsVisible = comment,
                    archived = submission.archived,
                    author = submission.author.orEmpty(),
                    bodyHtml = submission.bodyHtml,
                    createdUtc = submission.createdUtc,
                    domain = submission.domain,
                    fullname = submission.fullname,
                    gildedCount = submission.gilded,
                    hideable = submission.isHideable,
                    likes = submission.likes,
                    linkFlair = submission.linkFlairText,
                    linkTitle = submission.linkTitle,
                    linkUrl = submission.linkUrl.orEmpty(),
                    locked = submission.isLocked,
                    nsfw = submission.isOver18,
                    permalink = submission.permalink,
                    points = submission.score,
                    postHint = submission.postHint,
                    preview = submission.preview,
                    saved = submission.saved,
                    scoreHidden = submission.isScoreHidden,
                    stickied = submission.stickied,
                    subreddit = submission.subreddit)
        }
    }

    override fun children(): Observable<Node<Response<Thing<Listing>>>> = children

    override fun descendantCount(): Single<Int> =
            if (commentCount == null) super.descendantCount() else Single.just(commentCount)

    fun article(): String = fullname.substring(3, fullname.length)

    fun isGilded(): Boolean = gildedCount > 0

    fun hasLinkFlair(): Boolean = !linkFlair.isNullOrEmpty()

    fun getDisplayAge(): CharSequence = DateUtils.getRelativeTimeSpanString(
            TimeUnit.SECONDS.toMillis(createdUtc), System.currentTimeMillis(),
            DateUtils.SECOND_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_ALL or DateUtils.FORMAT_NO_NOON
                    or DateUtils.FORMAT_NO_MIDNIGHT or DateUtils.FORMAT_NO_MONTH_DAY)

    fun getDisplayBody(context: Context): Spannable? {
        if (bodyHtml.isNullOrEmpty()) {
            return null
        }
        val html: Spannable = SpannableString(Html.fromHtml(StringEscapeUtils.unescapeHtml4(bodyHtml)).trimEnd())
        Linkifier.addLinks(context, html)
        return html
    }

    fun getDisplayDescendants(): Single<String> = descendantCount()
            .map(this::quantityString)

    fun getDisplayPoints(context: Context): String {
        val resources: Resources = context.resources

        return if (scoreHidden) {
            resources.getString(R.string.score_hidden)
        } else {
            val formattedString: String = quantityString(points)
            resources.getQuantityString(R.plurals.points, points, formattedString)
        }
    }

    fun quantityString(num: Int): String = when {
        num < 1000 -> num.toString()
        else -> {
            val beforeDecimal: Int = num / 1000
            val afterDecimal: Int = num % 1000 / 100

            val maxStringSize: Int = 5 // e.g. "99.9K"
            val result: StringBuilder = StringBuilder(maxStringSize)
            result.append(beforeDecimal)
            if (afterDecimal > 0) {
                result.append('.').append(afterDecimal)
            }
            result.append('K')
            result.toString()
        }
    }

    fun flairs(context: Context): Collection<Flair> = listOf(
            stickied to Flair.Stickied(context),
            locked to Flair.Locked(context),
            nsfw to Flair.Nsfw(context),
            hasLinkFlair() to Flair.Link(context, linkFlair),
            isGilded() to Flair.Gilded(context, gildedCount))
            .filter { it.first }
            .map { it.second }

    override fun degree(): Int? {
        return null
    }
}
