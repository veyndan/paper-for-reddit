package com.veyndan.paper.reddit.post.media.model;

import android.content.Context;
import android.text.Spannable;

import rx.functions.Func1;

public class Text {

    private final Func1<Context, Spannable> body;

    public Text(final Func1<Context, Spannable> body) {
        this.body = body;
    }

    public Spannable getBody(final Context context) {
        return body.call(context);
    }
}
