package com.veyndan.paper.reddit.ui.widget

import android.content.Context
import android.content.Intent
import android.support.v7.widget.AppCompatTextView
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import com.binaryfork.spanny.Spanny
import com.veyndan.paper.reddit.MainActivity
import com.veyndan.paper.reddit.api.reddit.Reddit

class PostSubtitleView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    init {
        movementMethod = LinkMovementMethod.getInstance()
    }

    fun setSubtitle(author: String, age: CharSequence, subreddit: String) {
        val authorClickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                val intent: Intent = Intent(context, MainActivity::class.java)
                intent.putExtra(Reddit.FILTER, Reddit.Filter(
                        nodeDepth = 0,
                        userName = author,
                        userComments = true,
                        userSubmitted = true))
                context.startActivity(intent)
            }
        }

        val subredditClickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                val intent: Intent = Intent(context, MainActivity::class.java)
                intent.putExtra(Reddit.FILTER, Reddit.Filter(
                        nodeDepth = 0,
                        subredditName = subreddit))
                context.startActivity(intent)
            }
        }

        val delimiter: String = " · "
        val subtitle: Spanny = Spanny()
                .append(author, authorClickableSpan)
                .append(delimiter)
                .append(age)
                .append(delimiter)
                .append(subreddit, subredditClickableSpan)
                .append(" ")

        text = subtitle
    }
}
