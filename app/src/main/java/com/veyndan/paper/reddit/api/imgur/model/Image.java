package com.veyndan.paper.reddit.api.imgur.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

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
        return Objects.requireNonNull(link);
    }
}
