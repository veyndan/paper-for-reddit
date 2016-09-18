package com.veyndan.redditclient.post;

import com.veyndan.redditclient.MvpView;
import com.veyndan.redditclient.Tree;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

public interface PostMvpView extends MvpView {

    void appendNodes(List<Tree.Node<Post>> nodes);

    Tree.Node<Post> popNode();

    Tree.Node<Post> popNode(int index);

    void clearNodes();
}
