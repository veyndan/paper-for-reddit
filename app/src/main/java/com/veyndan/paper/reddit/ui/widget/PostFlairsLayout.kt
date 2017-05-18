package com.veyndan.paper.reddit.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

import com.google.android.flexbox.FlexboxLayout
import com.veyndan.paper.reddit.databinding.PostFlairBinding
import com.veyndan.paper.reddit.post.Flair

class PostFlairsLayout(context: Context, attrs: AttributeSet) : FlexboxLayout(context, attrs) {

    fun setFlairs(flairs: Collection<Flair>, subreddit: String) {
        removeAllViews()

        if (flairs.isEmpty()) {
            visibility = View.GONE
            return
        }

        for (flair in flairs) {
            val binding = PostFlairBinding.inflate(LayoutInflater.from(context), this, false)
            binding.postFlair.setFlair(flair, subreddit)
            addView(binding.root)
        }

        visibility = View.VISIBLE
    }
}
