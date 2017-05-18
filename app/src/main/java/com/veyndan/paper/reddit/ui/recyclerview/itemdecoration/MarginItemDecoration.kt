package com.veyndan.paper.reddit.ui.recyclerview.itemdecoration

import android.content.Context
import android.graphics.Rect
import android.support.annotation.DimenRes
import android.support.annotation.Px
import android.support.v7.widget.RecyclerView
import android.view.View

class MarginItemDecoration(context: Context, @DimenRes marginRes: Int) : RecyclerView.ItemDecoration() {

    @Px private val margin: Int = context.resources.getDimensionPixelOffset(marginRes)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State?) {
        val params = view.layoutParams as RecyclerView.LayoutParams
        val position = params.viewLayoutPosition
        val marginTop = if (position == 0) margin else 0
        outRect.set(margin, marginTop, margin, margin)
    }
}
