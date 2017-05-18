package com.veyndan.paper.reddit.ui.recyclerview.itemdecoration

import android.content.Context
import android.graphics.Rect
import android.support.annotation.DimenRes
import android.support.annotation.Px
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.veyndan.paper.reddit.util.Node

class TreeInsetItemDecoration(context: Context,
                              @DimenRes childInsetMultiplierRes: Int) : RecyclerView.ItemDecoration() {

    @Px private val childInsetMultiplier: Int = context.resources.getDimensionPixelOffset(childInsetMultiplierRes)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State?) {
        check(parent.adapter is ListDelegationAdapter<*>) { "RecyclerView's Adapter must implement ListDelegationAdapter<List<Node<?>>> in order for TreeInsetItemDecoration to be used as a decoration" }

        val listDelegationAdapter: ListDelegationAdapter<List<Node<*>>> = parent.adapter as ListDelegationAdapter<List<Node<*>>>
        val position: Int = parent.getChildAdapterPosition(view)
        val nodes: List<Node<*>> = listDelegationAdapter.items

        val inset: Int = if (position == RecyclerView.NO_POSITION) 0 else {
            val depth: Int = nodes[position].depth
            depth * childInsetMultiplier
        }

        outRect.set(inset, 0, 0, 0)
    }
}
