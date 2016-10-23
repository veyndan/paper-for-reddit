package com.veyndan.paper.reddit.tree.adapter;

import android.app.Activity;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

import retrofit2.Response;

public class TreeAdapter extends ListDelegationAdapter<List<Node<Response<Thing<Listing>>>>> {

    public TreeAdapter(final Activity activity, final List<Node<Response<Thing<Listing>>>> posts, final Reddit reddit) {
        delegatesManager
                .addDelegate(new TreeAdapterDelegate(activity, reddit))
                .addDelegate(new ProgressAdapterDelegate());

        setItems(posts);
    }
}
