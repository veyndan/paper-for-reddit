package com.veyndan.redditclient.post.media.delegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates2.AbsListItemAdapterDelegate;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.post.media.model.Text;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

public class TextAdapterDelegate
        extends AbsListItemAdapterDelegate<Text, Object, TextAdapterDelegate.TextViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull final Object item, final List<Object> items,
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
    protected void onBindViewHolder(@NonNull final Text text,
                                    @NonNull final TextViewHolder holder) {
        holder.textView.setText(trimTrailingWhitespace(Html.fromHtml(StringEscapeUtils.unescapeHtml4(text.getBodyHtml()))));
        holder.textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static CharSequence trimTrailingWhitespace(@NonNull final CharSequence source) {
        int i = source.length();

        // loop back to the first non-whitespace character
        do {
            i--;
        } while (i > 0 && Character.isWhitespace(source.charAt(i)));

        return source.subSequence(0, i + 1);
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        TextViewHolder(final View itemView) {
            super(itemView);

            textView = (TextView) itemView;
        }
    }
}