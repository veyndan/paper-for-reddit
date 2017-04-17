package com.veyndan.paper.reddit.node.something.post.media.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LinkImage {

    public abstract String url();

    public abstract String domain();

    public static LinkImage create(final String url, final String domain) {
        return new AutoValue_LinkImage(url, domain);
    }
}
