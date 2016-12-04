package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class More extends RedditObject {
    public int count;
    @Nullable public String parentId;
    @Nullable public String id;
    @Nullable public String name;
    @NonNull public List<String> children = new ArrayList<>();
}
