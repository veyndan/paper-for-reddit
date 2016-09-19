package com.veyndan.redditclient.ui.recyclerview.itemdecoration;

import android.graphics.Rect;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.veyndan.redditclient.post.DepthCalculatorDelegate;

public class TreeInsetItemDecoration extends RecyclerView.ItemDecoration {

    private final int childInsetMultiplier;

    public TreeInsetItemDecoration(@Px final int childInsetMultiplier) {
        this.childInsetMultiplier = childInsetMultiplier;
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                               final RecyclerView.State state) {
        if (parent.getAdapter() instanceof DepthCalculatorDelegate) {
            final DepthCalculatorDelegate depthCalculatorDelegate = (DepthCalculatorDelegate) parent.getAdapter();
            final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            final int position = layoutParams.getViewLayoutPosition();
            final int inset = depthCalculatorDelegate.depthForPosition(position) * childInsetMultiplier;
            outRect.set(inset, 0, 0, 0);
        } else {
            throw new IllegalStateException("RecyclerView's Adapter must implement " +
                    "DepthCalculatorDelegate in order for TreeInsetItemDecoration to be used as " +
                    "a decoration");
        }
    }
}
