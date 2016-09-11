package com.veyndan.redditclient.ui.recyclerview.itemdecoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MarginItemDecoration extends RecyclerView.ItemDecoration {

    private final int margin;

    public MarginItemDecoration(final Context context, @DimenRes final int marginRes) {
        margin = context.getResources().getDimensionPixelOffset(marginRes);
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
