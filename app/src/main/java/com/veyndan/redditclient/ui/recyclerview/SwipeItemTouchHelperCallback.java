package com.veyndan.redditclient.ui.recyclerview;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * An {@link ItemTouchHelper.Callback} that makes a {@link RecyclerView} horizontally swipeable.
 * By default, attaching this {@link ItemTouchHelper.Callback} won't make the items swipeable. To
 * do so, the {@link RecyclerView.ViewHolder}s which you want to make swipeable must implement
 * the {@link Swipeable} interface.
 *
 * @see Swipeable
 */
public class SwipeItemTouchHelperCallback extends ItemTouchHelper.Callback {

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(final RecyclerView recyclerView,
                                final RecyclerView.ViewHolder holder) {
        int swipeFlags = 0;
        if (holder instanceof Swipeable) {
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(final RecyclerView recyclerView, final RecyclerView.ViewHolder holder,
                          final RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder holder, final int direction) {
        ((Swipeable) holder).onSwipe();
    }

    @Override
    public void onChildDraw(final Canvas c, final RecyclerView recyclerView,
                            final RecyclerView.ViewHolder holder, final float dX, final float dY,
                            final int actionState, final boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            holder.itemView.setAlpha(1.0f - Math.abs(dX) / (float) (holder.itemView.getWidth()));
            holder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, holder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
