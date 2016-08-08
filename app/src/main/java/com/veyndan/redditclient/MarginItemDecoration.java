package com.veyndan.redditclient;

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
        super.getItemOffsets(outRect, view, parent, state);

        outRect.bottom += margin;
        outRect.left += margin;
        outRect.right += margin;

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top += margin;
        }
    }
}
