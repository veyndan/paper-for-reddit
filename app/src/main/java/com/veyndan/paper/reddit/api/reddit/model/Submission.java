package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.squareup.moshi.Json;
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;

import java.util.Collections;

import io.reactivex.Observable;

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
     * Returns {@code true} if the Submission has been clicked on before, else {@code false}.
     * Probably always returns {@code false}. If the submission is a comment, then {@code false}
     * is returned.
     * <p>
     * #inferred
     */
    public abstract boolean isClicked();

    /**
     * The domain of this submission. {@code self} posts will be {@code self.<subreddit>} while
     * other examples include {@code en.wikipedia.org} and {@code s3.amazon.com}. If the submission
     * is a comment, then {@code null} is returned.
     */
    public abstract String getDomain();

    /**
     * {@code true} if the submission is hidden by the logged in user. {@code false} if not logged
     * in or not hidden. If the submission is a comment, then {@code false} is returned as Reddit
     * doesn't support comment hiding (as of yet).
     */
    public abstract boolean isHidden();

    /**
     * The CSS class of the link's flair. If the submission is a Comment, then {@code null} is
     * returned.
     */
    public abstract String getLinkFlairCssClass();

    /**
     * The text of the link's flair. If the submission is a Comment, then {@code null} is returned.
     */
    public abstract String getLinkFlairText();

    /**
     * Whether the submission is locked (closed to new comments) or not. If the submission is a
     * comment, then {@code false} is returned.
     */
    public abstract boolean isLocked();

    /**
     * Used for streaming video. Detailed information about the video and it's origins are placed
     * here. If the submission is a Comment, then {@code null} is returned.
     */
    public abstract Media getMedia();

    /**
     * Used for streaming video. Technical embed specific information is found here. If the
     * submission is a Comment, then {@code null} is returned.
     */
    public abstract MediaEmbed getMediaEmbed();

    /**
     * The number of comments that belong to this link. includes removed comments. If the submission
     * is a Comment, then {@code 0} is returned.
     */
    @Nullable
    @IntRange(from = 0)
    public abstract Integer getNumComments();

    /**
     * {@code true} if the post is tagged as NSFW. {@code false} if otherwise. If the submission is
     * a Comment, then {@code false} is returned.
     */
    public abstract boolean isOver18();

    /**
     * Full URL to the thumbnail for this link. "self" if this is a self post. "default" if a
     * thumbnail is not available. If the submission is a Comment, then {@code null} is returned.
     */
    public abstract String getThumbnail();

    /**
     * #undocumented
     */
    public abstract Object getSuggestedSort();

    /**
     * #undocumented
     */
    public abstract Media getSecureMedia();

    /**
     * #undocumented
     */
    public abstract Object getFromKind();

    /**
     * #undocumented
     */
    public abstract Preview getPreview();

    /**
     * #undocumented
     */
    public abstract MediaEmbed getSecureMediaEmbed();

    /**
     * Returns a string that suggests the content of this link. As a hint, this is lossy and may be
     * inaccurate in some cases. If the submission is a Comment, then {@link PostHint#SELF} is
     * returned.
     * <p>
     * #inferred ({@code "post_hint"} defined at <a href="https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896">https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896</a>
     */
    public abstract PostHint getPostHint();

    /**
     * #undocumented
     */
    public abstract Object from();

    /**
     * #undocumented
     */
    public abstract Object fromId();

    /**
     * #undocumented
     */
    public abstract boolean quarantine();

    /**
     * #undocumented
     */
    public abstract boolean visited();

    /**
     * Returns the submissions's parent ID. If the Submission is a Link, then {@code null} is
     * returned, as no logical parent is available.
     *
     * @return Submission's parent ID.
     */
    public abstract String getParentId();

    /**
     * The comment forest/leaves exactly one below the current Submission.
     */
    public abstract Thing<Listing> getReplies();

    /**
     * The account name of the poster. {@code null} if this is a promotional link.
     */
    public String author;

    public abstract String getLinkAuthor();

    /**
     * The CSS class of the author's flair. subreddit specific.
     */
    @Json(name = "author_flair_css_class") public String authorFlairCssClass;

    /**
     * The text of the author's flair. subreddit specific.
     */
    @Json(name = "author_flair_text") public String authorFlairText;

    /**
     * Url of the permanent link.
     */
    public abstract String getPermalink();

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
     * The id of the subreddit in which the thing is located.
     */
    @Json(name = "subreddit_id") public String subredditId;

    /**
     * Indicates if link has been edited. Will be the edit timestamp if the link has been edited
     * and return {@code false} otherwise.
     */
    public Object edited;

    /**
     * To allow determining whether they have been distinguished by moderators/admins. If
     * {@code null} then not distinguished.
     */
    public Distinguished distinguished;

    /**
     * {@code true} if the post is set as the sticky in its subreddit.
     */
    public boolean stickied;

    /**
     * Who removed this submission. {@code null} if nobody or you are not a mod.
     */
    @Json(name = "banned_by") public String bannedBy;

    /**
     * #undocumented
     */
    @Json(name = "user_reports") private final Iterable<Object> userReports = Collections.emptyList();

    public Observable<Object> getUserReports() {
        return Observable.fromIterable(userReports);
    }

    /**
     * #undocumented
     */
    public String id;

    /**
     * ID of the link the submission is or is in.
     */
    public abstract String getLinkId();

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

    /**
     * #undocumented
     */
    @Json(name = "report_reasons") public Object reportReasons;

    /**
     * Who approved this submission. {@code null} if nobody or you are not a mod.
     */
    @Json(name = "approved_by") public String approvedBy;

    /**
     * #undocumented
     */
    @Json(name = "removal_reason") public Object removalReason;

    private String name;

    /**
     * #undocumented
     */
    public String getFullname() {
        return name;
    }

    /**
     * #undocumented
     */
    @Json(name = "mod_reports") private final Iterable<Object> modReports = Collections.emptyList();

    public Observable<Object> getModReports() {
        return Observable.fromIterable(modReports);
    }

    /**
     * How many times this submission has been reported, {@code null} if not a mod.
     */
    @Json(name = "num_reports") public Object numReports;

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
     * #undocumented
     */
    public abstract int getControversiality();

    /**
     * Returns whether this submission can be hidden. {@code true} for {@link Link} and
     * {@code false} for {@link Comment}.
     */
    public abstract boolean isHideable();
}
