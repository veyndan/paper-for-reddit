package com.veyndan.paper.reddit.api.reddit.model;

import java.util.Collections;

import io.reactivex.Observable;

public class Listing implements RedditObject {
    public String before;
    public String after;
    public String modhash;
    private final Iterable<RedditObject> children = Collections.emptyList();

    public Observable<RedditObject> getChildren() {
        return Observable.fromIterable(children);
    }
}
