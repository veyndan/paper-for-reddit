package com.veyndan.paper.reddit.post;

import android.app.Activity;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.post.delegate.MoreAdapterDelegate;
import com.veyndan.paper.reddit.post.delegate.PostAdapterDelegate;
import com.veyndan.paper.reddit.post.delegate.ProgressAdapterDelegate;
import com.veyndan.paper.reddit.post.delegate.post.PostHeaderAdapterDelegate;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DepthCalculatorDelegate {

    private final AdapterDelegatesManager<List<Node<Response<Thing<Listing>>>>> delegatesManager;
    private final List<Node<Response<Thing<Listing>>>> nodes;

    public PostAdapter(final Activity activity, final List<Node<Response<Thing<Listing>>>> nodes, final Reddit reddit) {
        this.nodes = nodes;

        delegatesManager = new AdapterDelegatesManager<>();

        delegatesManager
                .addDelegate(new PostHeaderAdapterDelegate())
                .addDelegate(new PostAdapterDelegate(this, activity, reddit))
                .addDelegate(new ProgressAdapterDelegate())
                .addDelegate(new MoreAdapterDelegate());
    }

    @Override
    @IntRange(from = 0)
    public int depthForPosition(@IntRange(from = 0) final int position) {
        return nodes.get(position).getDepth();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return delegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        delegatesManager.onBindViewHolder(nodes, position, holder);
    }

    @Override
    public int getItemCount() {
        return nodes.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return delegatesManager.getItemViewType(nodes, position);
    }
}

// TODO Wrap each element in the list of nodes above with an object that denotes what position and subposition it is in and how many subpositions there are in that position.
// With media where we don't know how many subpositions there are, we can listen to any insertions in the adapter and then update the items subposition count as we go. The subposition
// count is the number of subpositions at the current time, so if an album is being loaded, the number of subpositions is 2 i.e. the post header and actions.
// TODO We don't want to duplicate the items. When the position is requested, just iterate all the items up to the point and add the subposition count to get to what position and
// subposition is being requested. Later we can improve the algorithm.
