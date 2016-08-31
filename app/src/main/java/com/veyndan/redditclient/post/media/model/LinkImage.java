package com.veyndan.redditclient.post.media.model;

public class LinkImage {

    private final String url;
    private final String domain;

    public LinkImage(final String url, final String domain) {
        this.url = url;
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public String getDomain() {
        return domain;
    }
}
