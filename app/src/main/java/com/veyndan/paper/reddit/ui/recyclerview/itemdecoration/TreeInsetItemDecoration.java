package com.veyndan.paper.reddit.ui.recyclerview.itemdecoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class TreeInsetItemDecoration extends RecyclerView.ItemDecoration {

    @Px private final int childInsetMultiplier;

    public TreeInsetItemDecoration(@NonNull final Context context,
                                   @DimenRes final int childInsetMultiplierRes) {
        childInsetMultiplier = context.getResources().getDimensionPixelOffset(childInsetMultiplierRes);
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                               final RecyclerView.State state) {
        checkArgument(parent.getAdapter() instanceof ListDelegationAdapter,
                "RecyclerView's Adapter must implement ListDelegationAdapter<List<Node<?>>> in " +
                        "order for TreeInsetItemDecoration to be used as a decoration");

        final ListDelegationAdapter<List<Node<?>>> listDelegationAdapter =
                (ListDelegationAdapter<List<Node<?>>>) parent.getAdapter();
        final int position = parent.getChildAdapterPosition(view);
        final List<Node<?>> nodes = listDelegationAdapter.getItems();

        final int inset;
        if (position == RecyclerView.NO_POSITION) {
            inset = 0;
        } else {
            final Node<?> node = nodes.get(position);
            final int depth = node.getDepth();
            inset = depth * childInsetMultiplier;
        }

        outRect.set(inset, 0, 0, 0);
    }
}
