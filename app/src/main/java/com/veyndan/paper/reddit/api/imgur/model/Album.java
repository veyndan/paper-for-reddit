package com.veyndan.paper.reddit.api.imgur.model;

import android.support.annotation.NonNull;

import java.util.Collections;

import io.reactivex.Observable;

public class Album {

    @NonNull private final Iterable<Image> images = Collections.emptyList();

    @NonNull
    public Observable<Image> getImages() {
        return Observable.fromIterable(images);
    }
}
