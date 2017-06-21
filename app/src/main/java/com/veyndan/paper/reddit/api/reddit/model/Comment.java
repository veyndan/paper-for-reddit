package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

import static com.google.common.base.Preconditions.checkNotNull;

public class Comment extends Submission {

    @Json(name = "link_id") private String linkId;
    private Preview preview = new Preview();
    private Thing<Listing> replies;

    @Override
    public Thing<Listing> replies() {
        return checkNotNull(replies);
    }

    @Override
    public String domain() {
        return null;
    }

    @Override
    public String linkFlairText() {
        return null;
    }

    @Override
    public boolean locked() {
        return false;
    }

    @Override
    public Media media() {
        return null;
    }

    @Nullable
    @IntRange(from = 0)
    @Override
    public Integer numComments() {
        return null;
    }

    @Override
    public boolean over18() {
        return false;
    }

    @Override
    public Preview preview() {
        return preview;
    }

    @Override
    public PostHint postHint() {
        return PostHint.SELF;
    }

    /**
     * The slug of the url is omitted as otherwise a request to a {@link Link} is required, or
     * {@link Comment#linkTitle} parsing is required. Omission of the slug doesn't affect the
     * permalink endpoint.
     *
     * @see Submission#permalink()
     */
    @Override
    public String permalink() {
        return String.format("https://www.reddit.com/comments/%s//%s", linkId.split("_")[1], id);
    }

    @Override
    public boolean hideable() {
        return false;
    }

    @Override
    public String toString() {
        return author;
    }
}
