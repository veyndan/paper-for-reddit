package com.veyndan.paper.reddit.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.AppCompatTextView
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import com.binaryfork.spanny.Spanny
import com.veyndan.paper.reddit.MainActivity
import com.veyndan.paper.reddit.api.reddit.Reddit
import com.veyndan.paper.reddit.post.Flair

class PostFlairView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    init {
        movementMethod = LinkMovementMethod.getInstance()
    }

    fun setFlair(flair: Flair, subreddit: String) {
        (background as GradientDrawable).setColor(flair.backgroundColor)
        setCompoundDrawablesWithIntrinsicBounds(flair.icon, null, null, null)

        if (flair.searchable()) {
            text = Spanny.spanText(flair.text.orEmpty(), object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    val intent: Intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(Reddit.FILTER, Reddit.Filter(
                            nodeDepth = 0,
                            subredditName = subreddit,
                            searchQuery = flair.searchQuery!!))
                    context.startActivity(intent)
                }
            })
        } else {
            text = flair.text.orEmpty()
        }
    }
}
