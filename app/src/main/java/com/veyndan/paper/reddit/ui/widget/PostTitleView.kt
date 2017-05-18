package com.veyndan.paper.reddit.ui.widget

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.util.AttributeSet

import com.veyndan.paper.reddit.util.Linkifier

class PostTitleView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    init {
        movementMethod = LinkMovementMethod.getInstance()
        Linkifier.addLinks(context, text as Spannable)
    }

    fun setTitle(text: CharSequence) {
        setText(text)
        Linkifier.addLinks(context, getText() as Spannable)
    }
}
