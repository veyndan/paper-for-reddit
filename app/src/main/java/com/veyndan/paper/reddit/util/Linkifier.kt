package com.veyndan.paper.reddit.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import com.veyndan.paper.reddit.MainActivity
import com.veyndan.paper.reddit.api.reddit.Reddit

class Linkifier {

    companion object {

        /**
         * https://www.reddit.com/r/modhelp/comments/1gd1at/name_rules_when_trying_to_create_a_subreddit/cajcylg
         */
        private val SUBREDDIT_REGEX = Regex("""[^\w]/?[r|R]/([A-Za-z0-9]\w{1,20})""")

        /**
         * https://www.reddit.com/r/modhelp/comments/1gd1at/name_rules_when_trying_to_create_a_subreddit/cajcylg
         */
        private val USER_REGEX = Regex("""[^\w]/?[u|U]/([A-Za-z0-9]\w{1,20})""")

        /**
         * https://support.twitter.com/articles/101299
         */
        private val TWITTER_MENTION_REGEX = Regex("""@(\w{1,15})""")

        @JvmStatic
        fun addLinks(context: Context, spannable: Spannable) {
            addSubredditLinks(context, spannable)
            addUserLinks(context, spannable)
            addTwitterMentionLinks(context, spannable)
        }

        private fun addSubredditLinks(context: Context, spannable: Spannable) {
            SUBREDDIT_REGEX.findAll(spannable)
                    .map { matchResult -> matchResult.groups[1]!! }
                    .forEach { (subredditName, range) ->
                        spannable.setSpan(object : ClickableSpan() {
                            override fun onClick(view: View?) {
                                val subredditIntent: Intent = Intent(context.applicationContext, MainActivity::class.java)
                                subredditIntent.putExtra(Reddit.FILTER, Reddit.Filter(
                                        nodeDepth = 0,
                                        subredditName = subredditName))
                                context.startActivity(subredditIntent)
                            }
                        }, range.start, range.endInclusive + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
        }

        private fun addUserLinks(context: Context, spannable: Spannable) {
            USER_REGEX.findAll(spannable)
                    .map { matchResult -> matchResult.groups[1]!! }
                    .forEach { (userName, range) ->
                        spannable.setSpan(object : ClickableSpan() {
                            override fun onClick(view: View?) {
                                val profileIntent: Intent = Intent(context.applicationContext, MainActivity::class.java)
                                profileIntent.putExtra(Reddit.FILTER, Reddit.Filter(
                                        nodeDepth = 0,
                                        userName = userName,
                                        userComments = true,
                                        userSubmitted = true))
                                context.startActivity(profileIntent)
                            }
                        }, range.start, range.endInclusive + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
        }

        private fun addTwitterMentionLinks(context: Context, spannable: Spannable) {
            TWITTER_MENTION_REGEX.findAll(spannable)
                    .map { matchResult -> matchResult.groups[1]!! }
                    .forEach { (twitterUsername, range) ->
                        spannable.setSpan(object : ClickableSpan() {
                            override fun onClick(view: View?) {
                                val url: String = "https://twitter.com/$twitterUsername"
                                val intent: Intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                context.startActivity(intent)
                            }
                        }, range.start, range.endInclusive + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
        }
    }
}
