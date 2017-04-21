package com.veyndan.paper.reddit.api.reddit.model;

import java.util.Collections;

import io.reactivex.Observable;

public class CaptchaJson {
    private final Iterable<Object> errors = Collections.emptyList();
    public CaptchaData data;

    public Observable<Object> getErrors() {
        return Observable.fromIterable(errors);
    }
}
