package com.veyndan.paper.reddit.api.xkcd.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

public class XkcdComic {

    @Nullable private String img;

    @NonNull
    public String getImg() {
        return Objects.requireNonNull(img);
    }
}
