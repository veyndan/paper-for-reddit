package com.veyndan.paper.reddit.post.delegate;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.databinding.MoreItemBinding;
import com.veyndan.paper.reddit.post.model.Progress;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

import retrofit2.Response;

public class MoreAdapterDelegate extends AbsListItemAdapterDelegate<Progress, Node<Response<Thing<Listing>>>, MoreAdapterDelegate.MoreViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Node<Response<Thing<Listing>>> node,
                                    @NonNull final List<Node<Response<Thing<Listing>>>> nodes,
                                    final int position) {
        return node instanceof Progress && node.degree() != null;
    }

    @NonNull
    @Override
    public MoreViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final MoreItemBinding binding = MoreItemBinding.inflate(inflater, parent, false);
        return new MoreViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Progress progress,
                                    @NonNull final MoreViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final int count = progress.degree();
        final Resources resources = holder.itemView.getResources();

        holder.binding.moreText.setText(resources.getQuantityString(R.plurals.children, count, count));
    }

    class MoreViewHolder extends RecyclerView.ViewHolder {

        final MoreItemBinding binding;

        MoreViewHolder(final MoreItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
