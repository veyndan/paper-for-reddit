package com.veyndan.paper.reddit.post.media.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.veyndan.paper.reddit.databinding.PostMediaTextBinding;
import com.veyndan.paper.reddit.post.media.model.Text;

import java.util.List;

public class TextAdapterDelegate
        extends AbsListItemAdapterDelegate<Text, Object, TextAdapterDelegate.TextViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Object item, @NonNull final List<Object> items,
                                    final int position) {
        return item instanceof Text;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final PostMediaTextBinding binding = PostMediaTextBinding.inflate(inflater, parent, false);
        return new TextViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Text text, @NonNull final TextViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final Context context = holder.itemView.getContext();
        holder.binding.postMediaText.setText(text.getBody(context));
        holder.binding.postMediaText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {

        private final PostMediaTextBinding binding;

        TextViewHolder(final PostMediaTextBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
