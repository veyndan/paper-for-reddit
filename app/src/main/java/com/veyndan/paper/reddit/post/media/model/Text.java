package com.veyndan.paper.reddit.post.media.model;

import android.content.Context;
import android.text.Spannable;

import io.reactivex.functions.Function;

public class Text {

    private final Function<Context, Spannable> body;

    public Text(final Function<Context, Spannable> body) {
        this.body = body;
    }

    public Spannable getBody(final Context context) {
        try {
            return body.apply(context);
        } catch (final Exception e) {
            throw new IllegalStateException();
        }
    }
}
