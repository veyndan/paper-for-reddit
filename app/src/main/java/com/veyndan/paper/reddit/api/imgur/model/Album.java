package com.veyndan.paper.reddit.api.imgur.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Album {

    @NonNull private final List<Image> images = new ArrayList<>();

    @NonNull
    public List<Image> getImages() {
        return images;
    }
}
