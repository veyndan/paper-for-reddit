package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Listing {
    @Nullable public String before;
    @Nullable public String after;
    @Nullable public String modhash;
    @NonNull public final List<RedditObject> children = new ArrayList<>();
}
