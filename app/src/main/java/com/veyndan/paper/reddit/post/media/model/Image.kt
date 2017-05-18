package com.veyndan.paper.reddit.post.media.model

import android.support.annotation.StringRes
import android.util.Size
import com.veyndan.paper.reddit.R

data class Image(val url: String, var size: Size = Size(0, 0),
                 @StringRes val type: Int = Image.IMAGE_TYPE_STANDARD) {

    companion object {

        val IMAGE_TYPE_STANDARD: Int = -1
        @StringRes val IMAGE_TYPE_GIF: Int = R.string.post_media_image_type_gif
    }
}
