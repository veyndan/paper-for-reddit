package com.veyndan.paper.reddit.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import com.binaryfork.spanny.Spanny;
import com.google.common.base.MoreObjects;
import com.veyndan.paper.reddit.MainActivity;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.post.Flair;

public class PostFlairView extends AppCompatTextView {

    public PostFlairView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setFlair(final Flair flair, final String subreddit) {
        ((GradientDrawable) getBackground()).setColor(flair.backgroundColor());
        setCompoundDrawablesWithIntrinsicBounds(flair.icon(), null, null, null);

        if (flair.searchable()) {
            setText(Spanny.spanText(MoreObjects.firstNonNull(flair.text(), ""), new ClickableSpan() {
                @Override
                public void onClick(final View widget) {
                    final Context context = getContext();
                    final Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(Reddit.FILTER, Reddit.Filter.builder()
                            .nodeDepth(0)
                            .subredditName(subreddit)
                            .searchQuery(flair.searchQuery())
                            .build());
                    context.startActivity(intent);
                }
            }));
        } else {
            setText(MoreObjects.firstNonNull(flair.text(), ""));
        }
    }
}
