package com.veyndan.redditclient.ui.recyclerview.itemdecoration;

import android.graphics.Rect;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MarginItemDecoration extends RecyclerView.ItemDecoration {

    private final int margin;

    public MarginItemDecoration(@Px final int margin) {
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                               final RecyclerView.State state) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int position = params.getViewLayoutPosition();
        final int marginTop = position == 0 ? margin : 0;
        outRect.set(margin, marginTop, margin, margin);
    }
}
