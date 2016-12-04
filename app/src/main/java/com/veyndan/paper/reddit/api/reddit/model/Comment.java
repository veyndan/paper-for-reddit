package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

import io.reactivex.Maybe;

public class Comment extends Submission {

    private int controversiality;
    @Nullable private String linkAuthor;
    @Nullable private String linkId;
    @Nullable private String parentId;
    @NonNull private Preview preview = new Preview();
    @Nullable private Thing<Listing> replies;

    @NonNull
    @Override
    public Thing<Listing> getReplies() {
        return Objects.requireNonNull(replies);
    }

    @Override
    public boolean isClicked() {
        return false;
    }

    @NonNull
    @Override
    public Maybe<String> getDomain() {
        return Maybe.empty();
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @NonNull
    @Override
    public Maybe<String> getLinkFlairCssClass() {
        return Maybe.empty();
    }

    @NonNull
    @Override
    public Maybe<String> getLinkFlairText() {
        return Maybe.empty();
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @NonNull
    @Override
    public Maybe<Media> getMedia() {
        return Maybe.empty();
    }

    @NonNull
    @Override
    public Maybe<MediaEmbed> getMediaEmbed() {
        return Maybe.empty();
    }

    @NonNull
    @Override
    public Maybe<Integer> getNumComments() {
        return Maybe.empty();
    }

    @Override
    public boolean isOver18() {
        return false;
    }

    @NonNull
    @Override
    public Maybe<String> getThumbnail() {
        return Maybe.empty();
    }

    @NonNull
    @Override
    public Maybe<Object> getSuggestedSort() {
        return Maybe.empty();
    }

    @NonNull
    @Override
    public Maybe<Media> getSecureMedia() {
        return Maybe.empty();
    }

    @Nullable
    @Override
    public Object getFromKind() {
        throw new UnsupportedOperationException("Method intention unknown");
    }

    @NonNull
    @Override
    public Preview getPreview() {
        return preview;
    }

    @NonNull
    @Override
    public Maybe<MediaEmbed> getSecureMediaEmbed() {
        return Maybe.empty();
    }

    @NonNull
    @Override
    public PostHint getPostHint() {
        return PostHint.SELF;
    }

    @Nullable
    @Override
    public Object from() {
        throw new UnsupportedOperationException("Method intention unknown");
    }

    @Nullable
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

    @NonNull
    @Override
    public Maybe<String> getParentId() {
        return parentId == null ? Maybe.empty() : Maybe.just(parentId);
    }

    @Nullable
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
    @NonNull
    @Override
    public String getPermalink() {
        if (linkId == null) {
            throw new NullPointerException();
        } else {
            return String.format("https://www.reddit.com/comments/%s//%s", linkId.split("_")[1], id);
        }
    }

    @Nullable
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

    @NonNull
    @Override
    public String toString() {
        return author;
    }
}
