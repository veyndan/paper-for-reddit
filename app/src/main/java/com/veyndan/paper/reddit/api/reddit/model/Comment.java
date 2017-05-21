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
    public Thing<Listing> getReplies() {
        return checkNotNull(replies);
    }

    @Override
    public String getDomain() {
        return null;
    }

    @Override
    public String getLinkFlairText() {
        return null;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public Media getMedia() {
        return null;
    }

    @Nullable
    @IntRange(from = 0)
    @Override
    public Integer getNumComments() {
        return null;
    }

    @Override
    public boolean isOver18() {
        return false;
    }

    @Override
    public Preview getPreview() {
        return preview;
    }

    @Override
    public PostHint getPostHint() {
        return PostHint.SELF;
    }

    /**
     * The slug of the url is omitted as otherwise a request to a {@link Link} is required, or
     * {@link Comment#linkTitle} parsing is required. Omission of the slug doesn't affect the
     * permalink endpoint.
     *
     * @see Submission#getPermalink()
     */
    @Override
    public String getPermalink() {
        return String.format("https://www.reddit.com/comments/%s//%s", linkId.split("_")[1], id);
    }

    @Override
    public boolean isHideable() {
        return false;
    }

    @Override
    public String toString() {
        return author;
    }
}
