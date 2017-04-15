package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import retrofit2.Response;

public final class NextPageEvent {

    private final Node<Response<Thing<Listing>>> node;
    private final int position;

    public NextPageEvent(final Node<Response<Thing<Listing>>> node, final int position) {
        this.node = node;
        this.position = position;
    }

    public Node<Response<Thing<Listing>>> getNode() {
        return node;
    }

    public int getPosition() {
        return position;
    }
}
