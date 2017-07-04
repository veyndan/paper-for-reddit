package com.veyndan.paper.reddit.post.media.model

import android.content.Context
import android.text.Spannable

data class Text(val body: (Context) -> Spannable)
