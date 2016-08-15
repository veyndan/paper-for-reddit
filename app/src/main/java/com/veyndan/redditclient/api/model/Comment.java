package com.veyndan.redditclient.api.model;

import java.util.Objects;

public class Comment extends Submission {

    // Documented
    private String linkAuthor;
    private String linkId;
    public String parentId;
    private Thing<Listing> replies;

    /**
     * The comment nodes/leaves exactly one below the current Comment.
     */
    public Thing<Listing> getReplies() {
        return Objects.requireNonNull(replies);
    }

    // Undocumented
    public int controversiality;

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
}
