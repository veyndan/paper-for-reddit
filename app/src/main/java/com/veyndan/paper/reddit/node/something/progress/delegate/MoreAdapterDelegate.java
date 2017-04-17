package com.veyndan.paper.reddit.node.something.progress.delegate;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.node.Node;
import com.veyndan.paper.reddit.node.something.progress.Progress;

import java.util.List;

import retrofit2.Response;

public class MoreAdapterDelegate extends AbsListItemAdapterDelegate<Progress, Node<Response<Thing<Listing>>>, MoreAdapterDelegate.MoreViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Node<Response<Thing<Listing>>> node,
                                    @NonNull final List<Node<Response<Thing<Listing>>>> nodes,
                                    final int position) {
        return node instanceof Progress && node.getDegree() != null;
    }

    @NonNull
    @Override
    public MoreViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.more_item, parent, false);
        return new MoreViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Progress progress,
                                    @NonNull final MoreViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final int count = progress.getDegree();
        final Resources resources = holder.itemView.getResources();

        holder.textView.setText(resources.getQuantityString(R.plurals.children, count, count));
    }

    class MoreViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        MoreViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
