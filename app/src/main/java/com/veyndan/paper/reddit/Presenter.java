package com.veyndan.paper.reddit;

import android.support.annotation.NonNull;

public interface Presenter<V> {

    void attachView(@NonNull V view);

    void detachView();
}
