package com.veyndan.redditclient.api.xkcd.model;

public class XkcdComic {

    private final String img;

    public XkcdComic(final String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }
}
