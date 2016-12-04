package com.veyndan.paper.reddit.post.delegate;

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
import com.veyndan.paper.reddit.post.model.Progress;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

import retrofit2.Response;

public class MoreAdapterDelegate extends AbsListItemAdapterDelegate<Progress, Node<Response<Thing<Listing>>>, MoreAdapterDelegate.MoreViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Node<Response<Thing<Listing>>> node,
                                    @NonNull final List<Node<Response<Thing<Listing>>>> nodes,
                                    final int position) {
        return node instanceof Progress && node.getDegree().count().blockingGet() == 1L;
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
        progress.getDegree()
                .map(count -> {
                    final Resources resources = holder.itemView.getResources();
                    return resources.getQuantityString(R.plurals.children, count, count);
                })
                .subscribe(holder.textView::setText);
    }

    class MoreViewHolder extends RecyclerView.ViewHolder {

        @NonNull final TextView textView;

        MoreViewHolder(@NonNull final View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
