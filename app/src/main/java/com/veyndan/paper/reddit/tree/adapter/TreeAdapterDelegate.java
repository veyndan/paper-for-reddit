package com.veyndan.paper.reddit.tree.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.tree.model.Post;
import com.veyndan.paper.reddit.tree.model.Progress;
import com.veyndan.paper.reddit.tree.node.adapter.NodeAdapter;
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.TreeInsetItemDecoration;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

class TreeAdapterDelegate extends AbsListItemAdapterDelegate<Node<Response<Thing<Listing>>>, Node<Response<Thing<Listing>>>, TreeAdapterDelegate.TreeViewHolder> {

    private final Activity activity;
    private final Reddit reddit;

    TreeAdapterDelegate(final Activity activity, final Reddit reddit) {
        this.activity = activity;
        this.reddit = reddit;
    }

    @Override
    protected boolean isForViewType(@NonNull final Node<Response<Thing<Listing>>> node,
                                    @NonNull final List<Node<Response<Thing<Listing>>>> nodes,
                                    final int position) {
        return node instanceof Post;
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        ButterKnife.bind(this, parent);
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_tree, parent, false);
        return new TreeViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Node<Response<Thing<Listing>>> node,
                                    @NonNull final TreeViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final Context context = holder.itemView.getContext();
        holder.nodeView.addItemDecoration(new TreeInsetItemDecoration(context, R.dimen.post_child_inset_multiplier));
        holder.nodeView.setAdapter(new NodeAdapter(activity, node, reddit));
    }

    static class TreeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.node_recycler_view) RecyclerView nodeView;

        TreeViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
