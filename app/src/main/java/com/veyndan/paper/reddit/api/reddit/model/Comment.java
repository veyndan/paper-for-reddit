package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

import java.util.Objects;

public class Comment extends Submission {

    private int controversiality;
    @Json(name = "link_author") private String linkAuthor;
    @Json(name = "link_id") private String linkId;
    @Json(name = "parent_id") private String parentId;
    private Preview preview = new Preview();
    private Thing<Listing> replies;

    @Override
    public Thing<Listing> getReplies() {
        return Objects.requireNonNull(replies);
    }

    @Override
    public boolean isClicked() {
        return false;
    }

    @Override
    public String getDomain() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public String getLinkFlairCssClass() {
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

    @Override
    public MediaEmbed getMediaEmbed() {
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
    public String getThumbnail() {
        return null;
    }

    @Override
    public Object getSuggestedSort() {
        return null;
    }

    @Override
    public Media getSecureMedia() {
        return null;
    }

    @Override
    public Object getFromKind() {
        throw new UnsupportedOperationException("Method intention unknown");
    }

    @Override
    public Preview getPreview() {
        return preview;
    }

    @Override
    public MediaEmbed getSecureMediaEmbed() {
        return null;
    }

    @Override
    public PostHint getPostHint() {
        return PostHint.SELF;
    }

    @Override
    public Object from() {
        throw new UnsupportedOperationException("Method intention unknown");
    }

    @Override
    public Object fromId() {
        throw new UnsupportedOperationException("Method intention unknown");
    }

    @Override
    public boolean quarantine() {
        throw new UnsupportedOperationException("Method intention unknown");
    }

    @Override
    public boolean visited() {
        return false;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public String getLinkAuthor() {
        return linkAuthor;
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
    public String getLinkId() {
        return linkId;
    }

    @Override
    public int getControversiality() {
        return controversiality;
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
