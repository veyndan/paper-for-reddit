package com.veyndan.paper.reddit.api.reddit.model;

import java.util.Collections;

import io.reactivex.Observable;

public class Image {
    public final Source source = new Source();
    private Iterable<Source> resolutions = Collections.emptyList();
    public Object variants;
    public String id;

    public Observable<Source> getResolutions() {
        return Observable.fromIterable(resolutions);
    }
}
