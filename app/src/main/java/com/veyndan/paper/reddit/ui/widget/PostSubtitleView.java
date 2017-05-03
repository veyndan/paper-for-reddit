package com.veyndan.paper.reddit.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import com.binaryfork.spanny.Spanny;
import com.veyndan.paper.reddit.MainActivity;
import com.veyndan.paper.reddit.api.reddit.Reddit;

public class PostSubtitleView extends AppCompatTextView {

    public PostSubtitleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setSubtitle(final String author, final CharSequence age, final String subreddit) {
        final Context context = getContext();

        final ClickableSpan authorClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View widget) {
                final Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(Reddit.FILTER, Reddit.Filter.builder()
                        .nodeDepth(0)
                        .userName(author)
                        .userComments(true)
                        .userSubmitted(true)
                        .build());
                context.startActivity(intent);
            }
        };

        final ClickableSpan subredditClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View widget) {
                final Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(Reddit.FILTER, Reddit.Filter.builder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build());
                context.startActivity(intent);
            }
        };

        final String delimiter = " · ";
        final Spanny subtitle = new Spanny()
                .append(author, authorClickableSpan)
                .append(delimiter)
                .append(age)
                .append(delimiter)
                .append(subreddit, subredditClickableSpan)
                .append(" ");

        setText(subtitle);
    }
}
