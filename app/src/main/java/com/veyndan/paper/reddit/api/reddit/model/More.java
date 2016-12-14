package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class More extends RedditObject {

    private int count;
    @Nullable private String parentId;
    @Nullable private String id;
    @Nullable private String name;
    @NonNull private final List<String> children = new ArrayList<>();

    public int getCount() {
        return count;
    }

    @NonNull
    public String getParentId() {
        return Objects.requireNonNull(parentId);
    }

    @NonNull
    public String getId() {
        return Objects.requireNonNull(id);
    }

    @NonNull
    public String getName() {
        return Objects.requireNonNull(name);
    }

    @NonNull
    public List<String> getChildren() {
        return children;
    }
}
