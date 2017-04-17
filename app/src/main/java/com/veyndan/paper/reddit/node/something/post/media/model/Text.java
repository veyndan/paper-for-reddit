package com.veyndan.paper.reddit.node.something.post.media.model;

import android.content.Context;
import android.text.Spannable;

import com.google.auto.value.AutoValue;

import io.reactivex.functions.Function;

@AutoValue
public abstract class Text {

    abstract Function<Context, Spannable> body();

    public static Text create(final Function<Context, Spannable> body) {
        return new AutoValue_Text(body);
    }

    public Spannable getBody(final Context context) {
        try {
            return body().apply(context);
        } catch (final Exception e) {
            throw new IllegalStateException();
        }
    }
}
