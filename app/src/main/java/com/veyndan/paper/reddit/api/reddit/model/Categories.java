package com.veyndan.paper.reddit.api.reddit.model;

import java.util.Collections;

import io.reactivex.Observable;

public class Categories {
    private final Iterable<Category> categories = Collections.emptyList();

    public Observable<Category> getCategories() {
        return Observable.fromIterable(categories);
    }
}
