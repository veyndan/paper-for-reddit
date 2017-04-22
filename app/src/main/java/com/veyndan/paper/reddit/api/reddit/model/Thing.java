package com.veyndan.paper.reddit.api.reddit.model;

public class Thing<T> {

    public Kind kind;
    public T data;

    public Thing(final T data) {
        this.data = data;
    }
}
