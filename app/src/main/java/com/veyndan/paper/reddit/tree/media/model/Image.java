package com.veyndan.paper.reddit.tree.media.model;

public class Image {

    private final String url;
    private int width;
    private int height;

    public Image(final String url) {
        this(url, 0, 0);
    }

    public Image(final String url, final int width, final int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }
}
