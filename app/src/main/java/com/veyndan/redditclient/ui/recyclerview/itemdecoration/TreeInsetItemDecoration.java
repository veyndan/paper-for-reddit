package com.veyndan.redditclient.ui.recyclerview.itemdecoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.veyndan.redditclient.post.DepthCalculatorDelegate;

public class TreeInsetItemDecoration extends RecyclerView.ItemDecoration {

    @Px private final int childInsetMultiplier;

    public TreeInsetItemDecoration(@NonNull final Context context,
                                   @DimenRes final int childInsetMultiplierRes) {
        childInsetMultiplier = context.getResources().getDimensionPixelOffset(childInsetMultiplierRes);
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                               final RecyclerView.State state) {
        if (parent.getAdapter() instanceof DepthCalculatorDelegate) {
            final DepthCalculatorDelegate depthCalculatorDelegate = (DepthCalculatorDelegate) parent.getAdapter();
            final int position = parent.getChildAdapterPosition(view);

            final int inset = position == RecyclerView.NO_POSITION
                    ? 0
                    : depthCalculatorDelegate.depthForPosition(position) * childInsetMultiplier;

            outRect.set(inset, 0, 0, 0);
        } else {
            throw new IllegalStateException("RecyclerView's Adapter must implement " +
                    "DepthCalculatorDelegate in order for TreeInsetItemDecoration to be used as " +
                    "a decoration");
        }
    }
}
