package com.veyndan.paper.reddit.ui.recyclerview;

/**
 * If a {@link RecyclerView} has a {@link SwipeItemTouchHelperCallback} attached, then items in
 * that {@link RecyclerView} which wish to be swipeable should have their
 * {@link RecyclerView.ViewHolder} implement this interface.
 *
 * @see SwipeItemTouchHelperCallback
 */
interface Swipeable {

    /**
     * Return {@code true} if this post should be swipeable, else {@code false}.
     */
    fun swipeable(): Boolean

    /**
     * Called when the current {@link RecyclerView.ViewHolder} is swiped by the user.
     *
     * @see android.support.v7.widget.helper.ItemTouchHelper.Callback#onSwiped(RecyclerView.ViewHolder, int)
     */
    fun onSwipe()
}
