package com.veyndan.redditclient.post.delegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hannesdorfmann.adapterdelegates2.AbsListItemAdapterDelegate;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.util.Node;
import com.veyndan.redditclient.R;

import java.util.List;

import retrofit2.Response;

public class ProgressAdapterDelegate
        extends AbsListItemAdapterDelegate<Node<Response<Thing<Listing>>>, Node<Response<Thing<Listing>>>, ProgressAdapterDelegate.ProgressViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Node<Response<Thing<Listing>>> node,
                                    final List<Node<Response<Thing<Listing>>>> nodes, final int position) {
        return node.isStub() && !node.isChildCountAvailable();
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View progressView = inflater.inflate(R.layout.progress_item, parent, false);
        return new ProgressViewHolder(progressView);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Node<Response<Thing<Listing>>> node,
                                    @NonNull final ProgressViewHolder holder) {
    }

    final static class ProgressViewHolder extends RecyclerView.ViewHolder {

        final ProgressBar progress;

        ProgressViewHolder(final View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView;
        }
    }
}
