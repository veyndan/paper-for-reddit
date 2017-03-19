package com.veyndan.paper.reddit.image;

public interface Cache<T> {

    T get(String key);

    void set(String key, T value);
}
