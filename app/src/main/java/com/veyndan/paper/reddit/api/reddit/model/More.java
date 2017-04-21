package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

import java.util.Collections;

import io.reactivex.Observable;

public class More implements RedditObject {
    public int count;
    @Json(name = "parent_id") public String parentId;
    public String id;
    public String name;
    private final Iterable<String> children = Collections.emptyList();

    public Observable<String> getChildren() {
        return Observable.fromIterable(children);
    }
}
