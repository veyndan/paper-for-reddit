package com.veyndan.redditclient.ui.recyclerview.itemdecoration;

import android.graphics.Rect;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TreeInsetItemDecoration extends RecyclerView.ItemDecoration {

    private final List<Integer> insets = new ArrayList<>();
    private final int childInsetMultiplier;

    public TreeInsetItemDecoration(@Px final int childInsetMultiplier) {
        this.childInsetMultiplier = childInsetMultiplier;
    }

    public void setInsets(final List<Integer> insets) {
        this.insets.clear();
        this.insets.addAll(insets);
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view,
                               final RecyclerView parent,
                               final RecyclerView.State state) {
        final int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        final int inset = insets.get(position) * childInsetMultiplier;
        outRect.set(inset, 0, 0, 0);
    }
}
