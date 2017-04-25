package com.veyndan.paper.reddit.post.delegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.databinding.ProgressItemBinding;
import com.veyndan.paper.reddit.post.model.Progress;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

import retrofit2.Response;

public class ProgressAdapterDelegate
        extends AbsListItemAdapterDelegate<Progress, Node<Response<Thing<Listing>>>, ProgressAdapterDelegate.ProgressViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Node<Response<Thing<Listing>>> node,
                                    @NonNull final List<Node<Response<Thing<Listing>>>> nodes,
                                    final int position) {
        return node instanceof Progress && node.degree() == null;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ProgressItemBinding binding = ProgressItemBinding.inflate(inflater, parent, false);
        return new ProgressViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Progress progress,
                                    @NonNull final ProgressViewHolder holder,
                                    @NonNull final List<Object> payloads) {
    }

    static final class ProgressViewHolder extends RecyclerView.ViewHolder {

        private final ProgressItemBinding binding;

        ProgressViewHolder(final ProgressItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
