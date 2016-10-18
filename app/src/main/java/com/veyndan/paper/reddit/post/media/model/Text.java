package com.veyndan.paper.reddit.post.media.model;

public class Text {

    private final CharSequence body;

    public Text(final CharSequence body) {
        this.body = body;
    }

    public CharSequence getBody() {
        return body;
    }
}
