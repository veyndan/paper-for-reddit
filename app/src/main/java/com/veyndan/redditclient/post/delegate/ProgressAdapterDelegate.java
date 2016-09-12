package com.veyndan.redditclient.post.delegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hannesdorfmann.adapterdelegates2.AbsListItemAdapterDelegate;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.Tree;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

public class ProgressAdapterDelegate
        extends AbsListItemAdapterDelegate<Tree.Node<Post>, Tree.Node<Post>, ProgressAdapterDelegate.ProgressViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Tree.Node<Post> node,
                                    final List<Tree.Node<Post>> nodes, final int position) {
        return node.isStub();
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View progressView = inflater.inflate(R.layout.progress_item, parent, false);
        return new ProgressViewHolder(progressView);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Tree.Node<Post> node,
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
