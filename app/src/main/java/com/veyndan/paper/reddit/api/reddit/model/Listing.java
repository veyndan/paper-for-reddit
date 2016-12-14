package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Listing {

    @Nullable private String before;
    @Nullable private String after;
    @Nullable private String modhash;
    @NonNull private final List<RedditObject> children = new ArrayList<>();

    @NonNull
    public String getBefore() {
        return Objects.requireNonNull(before);
    }

    @NonNull
    public String getAfter() {
        return Objects.requireNonNull(after);
    }

    @NonNull
    public String getModhash() {
        return Objects.requireNonNull(modhash);
    }

    @NonNull
    public List<RedditObject> getChildren() {
        return children;
    }
}
