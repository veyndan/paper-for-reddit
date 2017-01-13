package com.veyndan.paper.reddit.post;

import com.veyndan.paper.reddit.MvpView;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

// TODO Remove all methods here and have one which is `Action render(ForestModel<T> forestModel)`.
//      This returns Action so as to be easier to use e.g. doOnNext(render(…)) with
//      Action vs doOnNext(it -> render(…)) with void.
// TODO Instead of defining it here, delete PostMvpView and define render() in MvpView. MvpView
//      has a type parameter which is the model, so it is still just as generic as before.
public interface PostMvpView<T> extends MvpView {

    void appendNode(Node<T> node);

    void appendNodes(List<? extends Node<T>> nodes);

    Node<T> popNode();

    Node<T> popNode(int index);

    void clearNodes();
}
