package com.veyndan.paper.reddit.api.reddit.model

import android.support.annotation.IntRange
import com.squareup.moshi.Json
import okhttp3.HttpUrl
import java.util.regex.Pattern

class Link(
        private val domain: String,
        @Json(name = "link_flair_text") private val linkFlairText: String,
        private val locked: Boolean,
        private val media: Media,
        @Json(name = "num_comments") private val numComments: Int,
        @Json(name = "over_18") private val over18: Boolean,
        private val preview: Preview = Preview(),
        @Json(name = "is_self") private val isSelf: Boolean,
        @Json(name = "post_hint") private var postHint: PostHint = PostHint.LINK,
        private val replies: Thing<Listing> = Thing(Listing()),
        private val permalink: String
) : Submission() {

    companion object {

        private val DIRECT_IMAGE_DOMAINS: List<String> = listOf(
                "i.imgur.com", "i.redd.it", "i.reddituploads.com", "pbs.twimg.com",
                "upload.wikimedia.org")
    }

    override fun domain() = domain

    override fun linkFlairText() = linkFlairText

    override fun locked() = locked

    override fun media() = media

    @IntRange(from = 0)
    override fun numComments() = numComments

    override fun over18() = over18

    override fun preview() = preview

    override fun postHint(): PostHint {
        if (isSelf) {
            postHint = PostHint.SELF
        } else if (Pattern.compile("(.jpg|.jpeg|.gif|.png)$").matcher(linkUrl).find()
                || DIRECT_IMAGE_DOMAINS.contains(HttpUrl.parse(linkUrl).host())) {
            postHint = PostHint.IMAGE
        }
        return postHint
    }

    override fun replies() = replies

    override fun permalink() = "https://www.reddit.com$permalink"

    override fun hideable() = true
}
