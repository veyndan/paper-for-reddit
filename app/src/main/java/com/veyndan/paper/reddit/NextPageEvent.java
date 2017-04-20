package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.util.Node;

public final class NextPageEvent<T> {

    private final Node<T> tree;

    public NextPageEvent(final Node<T> tree) {
        this.tree = tree;
    }

    public Node<T> getTree() {
        return tree;
    }
}
