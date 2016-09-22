package com.veyndan.redditclient.post.delegate;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates2.AbsListItemAdapterDelegate;
import com.veyndan.redditclient.util.Node;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.post.model.Stub;

import java.util.List;

public class MoreAdapterDelegate extends AbsListItemAdapterDelegate<Stub, Node, MoreAdapterDelegate.MoreViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Node node,
                                    final List<Node> nodes, final int position) {
        return node instanceof Stub && ((Stub) node).isChildCountAvailable();
    }

    @NonNull
    @Override
    public MoreViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.more_item, parent, false);
        return new MoreViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Stub stub,
                                    @NonNull final MoreViewHolder holder) {
        final int count = stub.getChildCount();
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
