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
import java.util.regex.Matcher
import java.util.regex.Pattern

class Linkifier {

    companion object {

        /**
         * https://www.reddit.com/r/modhelp/comments/1gd1at/name_rules_when_trying_to_create_a_subreddit/cajcylg
         */
        private val SUBREDDIT_PATTERN: Pattern = Pattern.compile("[^\\w]/?[r|R]/([A-Za-z0-9]\\w{1,20})")

        /**
         * https://www.reddit.com/r/modhelp/comments/1gd1at/name_rules_when_trying_to_create_a_subreddit/cajcylg
         */
        private val USER_PATTERN: Pattern = Pattern.compile("[^\\w]/?[u|U]/([A-Za-z0-9]\\w{1,20})")

        /**
         * https://support.twitter.com/articles/101299
         */
        private val TWITTER_MENTION_PATTERN: Pattern = Pattern.compile("@(\\w{1,15})")

        @JvmStatic
        fun addLinks(context: Context, spannable: Spannable) {
            addSubredditLinks(context, spannable)
            addUserLinks(context, spannable)
            addTwitterMentionLinks(context, spannable)
        }

        private fun addSubredditLinks(context: Context, spannable: Spannable) {
            val matcher: Matcher = SUBREDDIT_PATTERN.matcher(spannable)

            while (matcher.find()) {
                val subredditName: String = matcher.group(1)

                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(view: View?) {
                        val subredditIntent: Intent = Intent(context.applicationContext, MainActivity::class.java)
                        subredditIntent.putExtra(Reddit.FILTER, Reddit.Filter(
                                nodeDepth = 0,
                                subredditName = subredditName))
                        context.startActivity(subredditIntent)
                    }
                }, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        private fun addUserLinks(context: Context, spannable: Spannable) {
            // https://www.reddit.com/r/modhelp/comments/1gd1at/name_rules_when_trying_to_create_a_subreddit/cajcylg
            val matcher: Matcher = USER_PATTERN.matcher(spannable)

            while (matcher.find()) {
                val userName: String = matcher.group(1)

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
                }, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        private fun addTwitterMentionLinks(context: Context, spannable: Spannable) {
            val matcher: Matcher = TWITTER_MENTION_PATTERN.matcher(spannable)

            while (matcher.find()) {
                val twitterUsername: String = matcher.group(1)

                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(view: View?) {
                        val url: String = "https://twitter.com/$twitterUsername"
                        val intent: Intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        context.startActivity(intent)
                    }
                }, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
}
