package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;

public final class NextPageUiModel {

    public static NextPageUiModel node(final Node<Response<Thing<Listing>>> node) {
        return new NextPageUiModel(Collections.singletonList(node));
    }

    public static NextPageUiModel nodes(final List<Node<Response<Thing<Listing>>>> nodes) {
        return new NextPageUiModel(nodes);
    }

    private final List<Node<Response<Thing<Listing>>>> nodes;

    private NextPageUiModel(final List<Node<Response<Thing<Listing>>>> nodes) {
        this.nodes = nodes;
    }

    public List<Node<Response<Thing<Listing>>>> getNodes() {
        return nodes;
    }
}
