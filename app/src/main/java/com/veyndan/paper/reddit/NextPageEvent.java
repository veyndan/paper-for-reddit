package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import retrofit2.Response;

public final class NextPageEvent {

    private final Node<Response<Thing<Listing>>> node;

    public NextPageEvent(final Node<Response<Thing<Listing>>> node) {
        this.node = node;
    }

    public Node<Response<Thing<Listing>>> getNode() {
        return node;
    }
}
