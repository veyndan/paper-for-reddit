package com.veyndan.paper.reddit.post;

import com.veyndan.paper.reddit.MvpView;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

public interface PostMvpView<T> extends MvpView {

    void appendNode(Node<T> node);

    void appendNodes(List<? extends Node<T>> nodes);

    Node<T> popNode();

    Node<T> popNode(int index);

    void clearNodes();
}
