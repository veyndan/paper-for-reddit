package com.veyndan.redditclient.post.model.media;

public class Text {

    private final String bodyHtml;

    public Text(final String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }
}
