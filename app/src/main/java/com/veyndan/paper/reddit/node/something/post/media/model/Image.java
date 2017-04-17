package com.veyndan.paper.reddit.node.something.post.media.model;

import android.support.annotation.StringRes;
import android.util.Size;

import com.google.auto.value.AutoValue;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.util.MutableObject;

@AutoValue
public abstract class Image {

    public static final int IMAGE_TYPE_STANDARD = -1;
    @StringRes public static final int IMAGE_TYPE_GIF = R.string.post_media_image_type_gif;

    public abstract String url();

    public abstract MutableObject<Size> size();

    @StringRes
    public abstract int type();

    public static Image create(final String url) {
        return create(url, new Size(0, 0));
    }

    public static Image create(final String url, final Size size) {
        return create(url, size, IMAGE_TYPE_STANDARD);
    }

    public static Image create(final String url, final Size size, @StringRes final int type) {
        return new AutoValue_Image(url, new MutableObject<>(size), type);
    }
}
