package com.veyndan.paper.reddit.ui.recyclerview

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * An [ItemTouchHelper.Callback] that makes a [RecyclerView] horizontally swipeable.
 * By default, attaching this [ItemTouchHelper.Callback] won't make the items swipeable. To
 * do so, the [RecyclerView.ViewHolder]s which you want to make swipeable must implement
 * the [Swipeable] interface.

 * @see Swipeable
 */
class SwipeItemTouchHelperCallback : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView,
                                  holder: RecyclerView.ViewHolder): Int {
        val swipeFlags = if (holder is Swipeable && holder.swipeable())
            ItemTouchHelper.START or ItemTouchHelper.END
        else
            0
        return ItemTouchHelper.Callback.makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, holder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
        (holder as Swipeable).onSwipe()
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                             holder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            holder.itemView.alpha = 1.0f - Math.abs(dX) / holder.itemView.width.toFloat()
            holder.itemView.translationX = dX
        } else {
            super.onChildDraw(c, recyclerView, holder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}
