package com.veyndan.paper.reddit.post.media.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Link {

    public abstract String domain();

    public static Link create(final String domain) {
        return new AutoValue_Link(domain);
    }
}
