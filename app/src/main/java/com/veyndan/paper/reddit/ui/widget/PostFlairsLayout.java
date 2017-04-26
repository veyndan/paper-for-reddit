package com.veyndan.paper.reddit.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.flexbox.FlexboxLayout;
import com.veyndan.paper.reddit.databinding.PostFlairBinding;
import com.veyndan.paper.reddit.post.Flair;

import java.util.Collection;

public class PostFlairsLayout extends FlexboxLayout {

    public PostFlairsLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFlairs(final Collection<Flair> flairs, final String subreddit) {
        if (flairs.isEmpty()) {
            setVisibility(GONE);
            return;
        }

        for (final Flair flair : flairs) {
            final PostFlairBinding binding = PostFlairBinding.inflate(LayoutInflater.from(getContext()), this, false);
            binding.postFlair.setFlair(flair, subreddit);
            addView(binding.getRoot());
        }

        setVisibility(VISIBLE);
    }
}
