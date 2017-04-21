package com.veyndan.paper.reddit;

import com.google.auto.value.AutoValue;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import retrofit2.Response;

@AutoValue
public abstract class NextPageEvent {

    public abstract Node<Response<Thing<Listing>>> tree();

    public static NextPageEvent create(final Node<Response<Thing<Listing>>> tree) {
        return new AutoValue_NextPageEvent(tree);
    }
}
