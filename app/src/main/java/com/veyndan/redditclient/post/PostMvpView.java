package com.veyndan.redditclient.post;

import com.veyndan.redditclient.MvpView;
import com.veyndan.redditclient.Tree;

import java.util.List;

public interface PostMvpView extends MvpView {

    void appendNode(Tree.Node<?> node);

    void appendNodes(List<? extends Tree.Node<?>> nodes);

    Tree.Node<?> popNode();

    Tree.Node<?> popNode(int index);

    void clearNodes();
}
