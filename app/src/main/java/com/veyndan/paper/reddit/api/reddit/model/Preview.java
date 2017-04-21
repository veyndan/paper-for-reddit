package com.veyndan.paper.reddit.api.reddit.model;

import java.util.Collections;

import io.reactivex.Observable;

public class Preview {
    private final Iterable<Image> images = Collections.emptyList();

    public Observable<Image> getImages() {
        return Observable.fromIterable(images);
    }
}
