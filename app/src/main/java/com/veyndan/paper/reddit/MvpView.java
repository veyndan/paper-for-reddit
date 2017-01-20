package com.veyndan.paper.reddit;

import android.content.Context;

public interface MvpView<M extends Model> {

    Context getContext();

    void render(M m);
}
