package com.veyndan.redditclient;

public interface Presenter<V> {

    void attachView(V view);

    void detachView();
}
