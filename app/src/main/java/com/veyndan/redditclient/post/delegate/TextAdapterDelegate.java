package com.veyndan.redditclient.post.delegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates2.AdapterDelegate;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.post.model.media.Text;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

public class TextAdapterDelegate implements AdapterDelegate<List<Object>> {

    @Override
    public boolean isForViewType(@NonNull final List<Object> items, final int position) {
        return items.get(position) instanceof Text;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.post_media_text, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List<Object> items, final int position,
                                 @NonNull final RecyclerView.ViewHolder holder) {
        final TextViewHolder textViewHolder = (TextViewHolder) holder;
        final Text text = (Text) items.get(position);

        textViewHolder.textView.setText(trimTrailingWhitespace(Html.fromHtml(StringEscapeUtils.unescapeHtml4(text.getBodyHtml()))));
        textViewHolder.textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private CharSequence trimTrailingWhitespace(@NonNull final CharSequence source) {
        int i = source.length();

        // loop back to the first non-whitespace character
        do {
            i--;
        } while (i > 0 && Character.isWhitespace(source.charAt(i)));

        return source.subSequence(0, i + 1);
    }

    private static class TextViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        TextViewHolder(final View itemView) {
            super(itemView);

            textView = (TextView) itemView;
        }
    }
}
