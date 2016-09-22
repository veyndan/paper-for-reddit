package com.veyndan.redditclient.post;

import com.veyndan.redditclient.MvpView;
import com.veyndan.redditclient.util.Node;

import java.util.List;

public interface PostMvpView extends MvpView {

    void appendNode(Node node);

    void appendNodes(List<? extends Node> nodes);

    Node popNode();

    Node popNode(int index);

    void clearNodes();
}
