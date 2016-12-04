package com.veyndan.paper.reddit.post.media.model;

import android.support.annotation.NonNull;

public class LinkImage {

    @NonNull private final String url;
    @NonNull private final String domain;

    public LinkImage(@NonNull final String url, @NonNull final String domain) {
        this.url = url;
        this.domain = domain;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @NonNull
    public String getDomain() {
        return domain;
    }
}
