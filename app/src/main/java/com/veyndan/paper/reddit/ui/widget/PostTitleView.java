package com.veyndan.paper.reddit.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;

import com.veyndan.paper.reddit.util.Linkifier;

public class PostTitleView extends AppCompatTextView {

    public PostTitleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        setMovementMethod(LinkMovementMethod.getInstance());
        Linkifier.addLinks(context, (Spannable) getText());
    }

    public void setTitle(final CharSequence text) {
        setText(text);
        Linkifier.addLinks(getContext(), (Spannable) getText());
    }
}
