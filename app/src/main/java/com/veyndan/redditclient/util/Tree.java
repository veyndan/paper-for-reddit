package com.veyndan.redditclient.util;

import android.support.annotation.NonNull;

import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;

import java.util.List;

import retrofit2.Response;

public final class Tree {

    private static final DepthTreeTraverser<Node<Response<Thing<Listing>>>> TREE_TRAVERSER = new DepthTreeTraverser<Node<Response<Thing<Listing>>>>() {
        @Override
        public Iterable<Node<Response<Thing<Listing>>>> children(@NonNull final Node<Response<Thing<Listing>>> root) {
            return root.getChildren();
        }
    };

    @NonNull
    public static List<Node<Response<Thing<Listing>>>> flattenFrom(@NonNull final Node<Response<Thing<Listing>>> root) {
        return TREE_TRAVERSER.preOrderTraversal(root).transform(input -> {
            input.first.setDepth(input.second);
            return input.first;
        }).toList();
    }
}
