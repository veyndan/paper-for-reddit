package com.veyndan.paper.reddit.post;

import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.MvpView;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

public interface PostMvpView<T> extends MvpView {

    void appendNode(@NonNull Node<T> node);

    void appendNodes(@NonNull List<? extends Node<T>> nodes);

    @NonNull
    Node<T> popNode();

    @NonNull
    Node<T> popNode(int index);

    void clearNodes();
}
