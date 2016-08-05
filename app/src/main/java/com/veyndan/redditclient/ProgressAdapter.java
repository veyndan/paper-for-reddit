package com.veyndan.redditclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public abstract class ProgressAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Use negative numbers so view type conflicts with concrete child classes unlikely.
    private static final int TYPE_PROGRESS = -1;

    private static final int FOOTER_SIZE = 1;

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PROGRESS) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ProgressViewHolder(inflater.inflate(R.layout.progress_item, parent, false));
        } else {
            return onCreateContentViewHolder(parent, viewType);
        }
    }

    protected abstract T onCreateContentViewHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_PROGRESS) {
            // TODO: Do something with progress bar
        } else {
            onBindContentViewHolder((T) holder, position);
        }
    }

    protected abstract void onBindContentViewHolder(T holder, int position);

    @Override
    public final int getItemCount() {
        return getContentItemCount() + FOOTER_SIZE;
    }

    protected abstract int getContentItemCount();

    @Override
    public final int getItemViewType(int position) {
        return position == getItemCount() - 1 ? TYPE_PROGRESS : getContentItemViewType(position);
    }

    protected abstract int getContentItemViewType(int position);

    private final static class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progress;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView;
        }
    }
}
