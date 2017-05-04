package com.veyndan.paper.reddit.api.xkcd.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class XkcdComic {

    @Nullable private String img;

    @NonNull
    public String getImg() {
        return checkNotNull(img);
    }
}
