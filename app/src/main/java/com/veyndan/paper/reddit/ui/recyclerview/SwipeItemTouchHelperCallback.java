package com.veyndan.paper.reddit.ui.recyclerview;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
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
    public int getMovementFlags(@NonNull final RecyclerView recyclerView,
                                @NonNull final RecyclerView.ViewHolder holder) {
        final int swipeFlags = holder instanceof Swipeable && ((Swipeable) holder).swipeable()
                ? ItemTouchHelper.START | ItemTouchHelper.END
                : 0;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder holder,
                          @NonNull final RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder holder, final int direction) {
        ((Swipeable) holder).onSwipe();
    }

    @Override
    public void onChildDraw(@NonNull final Canvas c, @NonNull final RecyclerView recyclerView,
                            @NonNull final RecyclerView.ViewHolder holder, final float dX, final float dY,
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
