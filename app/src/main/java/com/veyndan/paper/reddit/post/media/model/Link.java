package com.veyndan.paper.reddit.post.media.model;

import android.support.annotation.NonNull;

public class Link {

    @NonNull private final String domain;

    public Link(@NonNull final String domain) {
        this.domain = domain;
    }

    @NonNull
    public String getDomain() {
        return domain;
    }
}
