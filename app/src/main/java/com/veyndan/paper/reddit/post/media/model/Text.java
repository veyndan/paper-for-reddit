package com.veyndan.paper.reddit.post.media.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;

import io.reactivex.functions.Function;

public class Text {

    @NonNull private final Function<Context, Spannable> body;

    public Text(@NonNull final Function<Context, Spannable> body) {
        this.body = body;
    }

    @NonNull
    public Spannable getBody(@NonNull final Context context) {
        try {
            return body.apply(context);
        } catch (final Exception e) {
            throw new IllegalStateException();
        }
    }
}
