package com.veyndan.paper.reddit.tree.node.adapter;

import android.app.Activity;
import android.support.annotation.IntRange;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.tree.DepthCalculatorDelegate;
import com.veyndan.paper.reddit.util.Node;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;

public class NodeAdapter extends ListDelegationAdapter<List<Node<Response<Thing<Listing>>>>>
        implements DepthCalculatorDelegate {

    public NodeAdapter(final Activity activity, final Node<Response<Thing<Listing>>> node, final Reddit reddit) {
        delegatesManager
                .addDelegate(new NodeAdapterDelegate(this, activity, reddit))
                .addDelegate(new MoreAdapterDelegate());

        setItems(Collections.singletonList(node));
    }

    @Override
    @IntRange(from = 0)
    public int depthForPosition(@IntRange(from = 0) final int position) {
        return items.get(position).getDepth();
    }
}
