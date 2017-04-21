package com.veyndan.paper.reddit;

import com.google.auto.value.AutoValue;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;

@AutoValue
public abstract class NextPageUiModel {

    public static NextPageUiModel tree(final Node<Response<Thing<Listing>>> tree) {
        return forest(Collections.singletonList(tree));
    }

    public static NextPageUiModel forest(final List<Node<Response<Thing<Listing>>>> forest) {
        return new AutoValue_NextPageUiModel(forest);
    }

    public abstract List<Node<Response<Thing<Listing>>>> forest();
}
