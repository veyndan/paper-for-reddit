package com.veyndan.redditclient.post.media.model;

public class Text {

    private final String bodyHtml;

    public Text(final String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }
}
