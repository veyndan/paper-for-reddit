package com.veyndan.paper.reddit.post.media.delegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.veyndan.paper.reddit.R;
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
        final View view = inflater.inflate(R.layout.post_media_text, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Text text, @NonNull final TextViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        holder.textView.setText(text.getBody());
        holder.textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        TextViewHolder(final View itemView) {
            super(itemView);

            textView = (TextView) itemView;
        }
    }
}
