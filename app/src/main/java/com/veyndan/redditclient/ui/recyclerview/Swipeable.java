package com.veyndan.redditclient.ui.recyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * If a {@link RecyclerView} has a {@link SwipeItemTouchHelperCallback} attached, then items in
 * that {@link RecyclerView} which wish to be swipeable should have their
 * {@link RecyclerView.ViewHolder} implement this interface.
 *
 * @see SwipeItemTouchHelperCallback
 */
public interface Swipeable {

    /**
     * Return {@code true} if this post should be swipeable, else {@code false}.
     */
    boolean swipeable();

    /**
     * Called when the current {@link RecyclerView.ViewHolder} is swiped by the user.
     * @see android.support.v7.widget.helper.ItemTouchHelper.Callback#onSwiped(RecyclerView.ViewHolder, int)
     */
    void onSwipe();
}
