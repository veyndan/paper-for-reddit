package com.veyndan.redditclient.post;

import com.veyndan.redditclient.MvpView;
import com.veyndan.redditclient.util.Node;

import java.util.List;

public interface PostMvpView<T> extends MvpView {

    void appendNode(Node<T> node);

    void appendNodes(List<? extends Node<T>> nodes);

    Node<T> popNode();

    Node<T> popNode(int index);

    void clearNodes();
}
