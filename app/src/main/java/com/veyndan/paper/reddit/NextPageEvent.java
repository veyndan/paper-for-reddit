package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.node.Node;

import retrofit2.Response;

public final class NextPageEvent {

    private final Node<Response<Thing<Listing>>> tree;

    public NextPageEvent(final Node<Response<Thing<Listing>>> tree) {
        this.tree = tree;
    }

    public Node<Response<Thing<Listing>>> getTree() {
        return tree;
    }
}
