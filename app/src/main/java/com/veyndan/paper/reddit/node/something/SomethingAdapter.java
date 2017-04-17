package com.veyndan.paper.reddit.node.something;

import android.app.Activity;
import android.support.annotation.IntRange;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.node.Node;
import com.veyndan.paper.reddit.node.something.progress.delegate.MoreAdapterDelegate;
import com.veyndan.paper.reddit.node.something.post.delegate.PostAdapterDelegate;
import com.veyndan.paper.reddit.node.something.progress.delegate.ProgressAdapterDelegate;

import java.util.List;

import retrofit2.Response;

public class SomethingAdapter extends ListDelegationAdapter<List<Node<Response<Thing<Listing>>>>>
        implements DepthCalculatorDelegate {

    public SomethingAdapter(final Activity activity, final List<Node<Response<Thing<Listing>>>> posts, final Reddit reddit) {
        delegatesManager
                .addDelegate(new PostAdapterDelegate(this, activity, reddit))
                .addDelegate(new ProgressAdapterDelegate())
                .addDelegate(new MoreAdapterDelegate());

        setItems(posts);
    }

    @Override
    @IntRange(from = 0)
    public int depthForPosition(@IntRange(from = 0) final int position) {
        return items.get(position).getDepth();
    }
}
