package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.squareup.moshi.Json;
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;

public abstract class Submission implements Created, RedditObject, Votable {

    private long created;
    @Json(name = "created_utc") private long createdUtc;
    private int ups;
    private int downs;

    /**
     * How the logged-in user has voted on the submission. {@code true} = upvoted,
     * {@code false} = downvoted, {@code null} = no vote.
     */
    private Boolean likes;

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public long getCreatedUtc() {
        return createdUtc;
    }

    @Override
    public int getUps() {
        return ups;
    }

    @Override
    public int getDowns() {
        return downs;
    }

    @Override
    public VoteDirection getLikes() {
        if (likes == null) {
            return VoteDirection.UNVOTE;
        }
        return likes ? VoteDirection.UPVOTE : VoteDirection.DOWNVOTE;
    }

    /**
     * The domain of this submission. {@code self} posts will be {@code self.<subreddit>} while
     * other examples include {@code en.wikipedia.org} and {@code s3.amazon.com}. If the submission
     * is a comment, then {@code null} is returned.
     */
    public abstract String domain();

    /**
     * The text of the link's flair. If the submission is a Comment, then {@code null} is returned.
     */
    public abstract String linkFlairText();

    /**
     * Whether the submission is locked (closed to new comments) or not. If the submission is a
     * comment, then {@code false} is returned.
     */
    public abstract boolean locked();

    /**
     * Used for streaming video. Detailed information about the video and it's origins are placed
     * here. If the submission is a Comment, then {@code null} is returned.
     */
    public abstract Media media();

    /**
     * The number of comments that belong to this link. includes removed comments. If the submission
     * is a Comment, then {@code 0} is returned.
     */
    @Nullable
    @IntRange(from = 0)
    public abstract Integer numComments();

    /**
     * {@code true} if the post is tagged as NSFW. {@code false} if otherwise. If the submission is
     * a Comment, then {@code false} is returned.
     */
    public abstract boolean over18();

    /**
     * #undocumented
     */
    public abstract Preview preview();

    /**
     * Returns a string that suggests the content of this link. As a hint, this is lossy and may be
     * inaccurate in some cases. If the submission is a Comment, then {@link PostHint#SELF} is
     * returned.
     * <p>
     * #inferred ({@code "post_hint"} defined at <a href="https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896">https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896</a>
     */
    public abstract PostHint postHint();

    /**
     * The comment forest/leaves exactly one below the current Submission.
     */
    public abstract Thing<Listing> replies();

    /**
     * The account name of the poster. {@code null} if this is a promotional link.
     */
    public String author;

    /**
     * Url of the permanent link.
     */
    public abstract String permalink();

    /**
     * {@code true} if this post is saved by the logged in user.
     */
    public boolean saved;

    /**
     * The net-score of the link.
     * <p>
     * Note: A submission's score is simply the number of upvotes minus
     * the number of downvotes. If five users like the submission and three users don't it will
     * have a score of 2. Please note that the vote numbers are not "real" numbers, they have been
     * "fuzzed" to prevent spam bots etc. So taking the above example, if five users upvoted the
     * submission, and three users downvote it, the upvote/downvote numbers may say 23 upvotes and
     * 21 downvotes, or 12 upvotes, and 10 downvotes. The points score is correct, but the vote
     * totals are "fuzzed".
     */
    public int score;

    /**
     * Subreddit of thing excluding the /r/ prefix e.g. "pics".
     */
    public String subreddit;

    /**
     * {@code true} if the post is set as the sticky in its subreddit.
     */
    public boolean stickied;

    /**
     * #undocumented
     */
    public String id;

    /**
     * The number of times this submission received Reddit Gold.
     */
    public int gilded;

    /**
     * Is this link archived.
     * <p>
     * #inferred
     */
    public boolean archived;

    private String name;

    /**
     * #undocumented
     */
    public String getFullname() {
        return name;
    }

    @Json(name = "score_hidden") private boolean scoreHidden;
    @Json(name = "hide_score") private boolean hideScore;

    /**
     * Should the score be hidden.
     * <p>
     * #inferred
     */
    public boolean isScoreHidden() {
        return scoreHidden || hideScore;
    }

    @Json(name = "link_title") private String linkTitle;
    private String title;

    /**
     * The title of the link. May contain newlines for some reason.
     */
    public String getLinkTitle() {
        return linkTitle != null ? linkTitle : title;
    }

    @Json(name = "link_url") private String linkUrl;
    private String url;

    /**
     * The link of this post. The permalink if this is a self-post.
     */
    public String getLinkUrl() {
        return linkUrl != null ? linkUrl : url;
    }

    private String body = "";
    @Json(name = "selftext") private String selfText = "";

    /**
     * The raw text. This is the unformatted text which includes the raw markup characters such as
     * ** for bold. <, >, and & are escaped.
     * <p>
     * If it is a {@link Link} then this is the self text if available. Empty if not present.
     */
    public String getBody() {
        return selfText != null ? selfText : body;
    }

    @Json(name = "body_html") private String bodyHtml;
    @Json(name = "selftext_html") private String selfTextHtml;

    /**
     * The formatted HTML text as displayed on reddit. For example, text that is emphasised by *
     * will now have <em> tags wrapping it. Additionally, bullets and numbered lists will now be in
     * HTML list format. NOTE: The HTML string will be escaped. You must unescape to get the raw
     * HTML.
     * <p>
     * If it is a {@link Link} then this is the self text if available. {@code null} if not present.
     */
    public String getBodyHtml() {
        return bodyHtml != null ? bodyHtml : selfTextHtml;
    }

    /**
     * Returns whether this submission can be hidden. {@code true} for {@link Link} and
     * {@code false} for {@link Comment}.
     */
    public abstract boolean hideable();
}
