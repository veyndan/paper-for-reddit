package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;

import static com.google.common.base.Preconditions.checkNotNull;

@AutoValue
public abstract class Comment implements Submission {

    @Override
    public VoteDirection voteDirection() {
        if (likes() == null) {
            return VoteDirection.UNVOTE;
        }
        return likes() ? VoteDirection.UPVOTE : VoteDirection.DOWNVOTE;
    }

    @Override
    public boolean clicked() {
        return false;
    }

    @Override
    public String domain() {
        return null;
    }

    @Override
    public boolean hidden() {
        return false;
    }

    @Override
    public String linkFlairCssClass() {
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

    @Override
    public MediaEmbed mediaEmbed() {
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
    public String thumbnail() {
        return null;
    }

    @Override
    public Object suggestedSort() {
        return null;
    }

    @Override
    public Media secureMedia() {
        return null;
    }

    @Override
    public Object fromKind() {
        throw new UnsupportedOperationException("Method intention unknown");
    }

    @Override
    public MediaEmbed secureMediaEmbed() {
        return null;
    }

    //
    private int controversiality;
    @Json(name = "link_author") private String linkAuthor;
    @Json(name = "link_id") private String linkId;

    @Json(name = "parent_id") private String parentId;

    private Thing<Listing> replies;

    @Override
    public Thing<Listing> getReplies() {
        return checkNotNull(replies);
    }

    @Override
    public PostHint postHint() {
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
        return String.format("https://www.reddit.com/comments/%s//%s", linkId.split("_")[1], id());
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

    public static JsonAdapter<Comment> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Comment.MoshiJsonAdapter(moshi);
    }
}
