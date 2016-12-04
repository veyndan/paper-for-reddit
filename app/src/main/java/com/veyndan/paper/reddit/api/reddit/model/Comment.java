package com.veyndan.paper.reddit.api.reddit.model;

import java.util.Objects;

import io.reactivex.Maybe;

public class Comment extends Submission {

    private int controversiality;
    private String linkAuthor;
    private String linkId;
    private String parentId;
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
    public Maybe<String> getDomain() {
        return Maybe.empty();
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public Maybe<String> getLinkFlairCssClass() {
        return Maybe.empty();
    }

    @Override
    public Maybe<String> getLinkFlairText() {
        return Maybe.empty();
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public Maybe<Media> getMedia() {
        return Maybe.empty();
    }

    @Override
    public Maybe<MediaEmbed> getMediaEmbed() {
        return Maybe.empty();
    }

    @Override
    public Maybe<Integer> getNumComments() {
        return Maybe.empty();
    }

    @Override
    public boolean isOver18() {
        return false;
    }

    @Override
    public Maybe<String> getThumbnail() {
        return Maybe.empty();
    }

    @Override
    public Maybe<Object> getSuggestedSort() {
        return Maybe.empty();
    }

    @Override
    public Maybe<Media> getSecureMedia() {
        return Maybe.empty();
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
    public Maybe<MediaEmbed> getSecureMediaEmbed() {
        return Maybe.empty();
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
    public Maybe<String> getParentId() {
        return Maybe.just(parentId);
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
