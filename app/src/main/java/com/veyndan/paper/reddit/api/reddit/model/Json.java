package com.veyndan.paper.reddit.api.reddit.model;

import java.util.Collections;

import io.reactivex.Observable;

public class Json {
    private Iterable<Object> errors = Collections.emptyList();
    public Things data;

    public Observable<Object> getErrors() {
        return Observable.fromIterable(errors);
    }
}
