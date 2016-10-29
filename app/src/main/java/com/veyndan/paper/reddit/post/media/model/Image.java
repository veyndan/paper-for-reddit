package com.veyndan.paper.reddit.post.media.model;

import android.util.Size;

public class Image {

    private final String url;
    private Size size;

    public Image(final String url) {
        this(url, new Size(0, 0));
    }

    public Image(final String url, final Size size) {
        this.url = url;
        this.size = size;
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
