package com.veyndan.paper.reddit.post.media.model;

import android.support.annotation.StringRes;
import android.util.Size;

import com.veyndan.paper.reddit.R;

public class Image {

    public static final int IMAGE_TYPE_STANDARD = -1;
    @StringRes public static final int IMAGE_TYPE_GIF = R.string.post_media_image_type_gif;

    private final int type;
    private final String url;
    private Size size;

    public Image(final String url) {
        this(url, new Size(0, 0));
    }

    public Image(final String url, final Size size) {
        this(url, size, IMAGE_TYPE_STANDARD);
    }

    public Image(final String url, final Size size, @StringRes final int type) {
        this.url = url;
        this.size = size;
        this.type = type;
    }

    @StringRes
    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(final Size size) {
        this.size = size;
    }
}
