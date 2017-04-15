package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;

public final class NextPageUiModel {

    public static NextPageUiModel tree(final Node<Response<Thing<Listing>>> tree) {
        return new NextPageUiModel(Collections.singletonList(tree));
    }

    public static NextPageUiModel forest(final List<Node<Response<Thing<Listing>>>> forest) {
        return new NextPageUiModel(forest);
    }

    private final List<Node<Response<Thing<Listing>>>> forest;

    private NextPageUiModel(final List<Node<Response<Thing<Listing>>>> forest) {
        this.forest = forest;
    }

    public List<Node<Response<Thing<Listing>>>> getForest() {
        return forest;
    }
}
