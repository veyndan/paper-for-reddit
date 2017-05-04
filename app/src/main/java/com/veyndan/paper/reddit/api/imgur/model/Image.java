package com.veyndan.paper.reddit.api.imgur.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class Image {

    private int width;
    private int height;
    @Nullable private String link;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @NonNull
    public String getLink() {
        return checkNotNull(link);
    }
}
