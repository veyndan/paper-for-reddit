package com.veyndan.redditclient.post.delegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hannesdorfmann.adapterdelegates2.AbsListItemAdapterDelegate;
import com.veyndan.redditclient.util.Node;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.post.model.Stub;

import java.util.List;

public class ProgressAdapterDelegate
        extends AbsListItemAdapterDelegate<Node<Stub>, Node<?>, ProgressAdapterDelegate.ProgressViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Node<?> node,
                                    final List<Node<?>> nodes, final int position) {
        return node.getData() instanceof Stub && !((Stub) node.getData()).isChildCountAvailable();
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View progressView = inflater.inflate(R.layout.progress_item, parent, false);
        return new ProgressViewHolder(progressView);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Node<Stub> node,
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
