package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Image {
    @NonNull public final Source source = new Source();
    @NonNull public List<Source> resolutions = new ArrayList<>();
    @Nullable public Object variants;
    @Nullable public String id;
}
