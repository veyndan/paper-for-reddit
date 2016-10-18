package com.veyndan.paper.reddit;

public interface Presenter<V> {

    void attachView(V view);

    void detachView();
}
