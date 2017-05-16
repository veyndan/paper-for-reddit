package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.squareup.moshi.Json;
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;

import java.util.List;

public interface Submission extends Created, RedditObject, Votable {

    VoteDirection voteDirection();

    /**
     * Returns {@code true} if the Submission has been clicked on before, else {@code false}.
     * Probably always returns {@code false}. If the submission is a comment, then {@code false}
     * is returned.
     * <p>
     * #inferred
     */
    boolean clicked();

    /**
     * The domain of this submission. {@code self} posts will be {@code self.<subreddit>} while
     * other examples include {@code en.wikipedia.org} and {@code s3.amazon.com}. If the submission
     * is a comment, then {@code null} is returned.
     */
    String domain();

    /**
     * {@code true} if the submission is hidden by the logged in user. {@code false} if not logged
     * in or not hidden. If the submission is a comment, then {@code false} is returned as Reddit
     * doesn't support comment hiding (as of yet).
     */
    boolean hidden();

    /**
     * The CSS class of the link's flair. If the submission is a Comment, then {@code null} is
     * returned.
     */
    @Json(name = "link_flair_css_class")
    String linkFlairCssClass();

    /**
     * The text of the link's flair. If the submission is a Comment, then {@code null} is returned.
     */
    @Json(name = "link_flair_text")
    String linkFlairText();

    /**
     * Whether the submission is locked (closed to new comments) or not. If the submission is a
     * comment, then {@code false} is returned.
     */
    boolean locked();

    /**
     * Used for streaming video. Detailed information about the video and it's origins are placed
     * here. If the submission is a Comment, then {@code null} is returned.
     */
    Media media();

    /**
     * Used for streaming video. Technical embed specific information is found here. If the
     * submission is a Comment, then {@code null} is returned.
     */
    @Json(name = "media_embed")
    MediaEmbed mediaEmbed();

    /**
     * The number of comments that belong to this link. includes removed comments. If the submission
     * is a Comment, then {@code 0} is returned.
     */
    @Nullable
    @IntRange(from = 0)
    @Json(name = "num_comments")
    Integer numComments();

    /**
     * {@code true} if the post is tagged as NSFW. {@code false} if otherwise. If the submission is
     * a Comment, then {@code false} is returned.
     */
    @Json(name = "over_18")
    boolean over18();

    /**
     * Full URL to the thumbnail for this link. "self" if this is a self post. "default" if a
     * thumbnail is not available. If the submission is a Comment, then {@code null} is returned.
     */
    String thumbnail();

    /**
     * #undocumented
     */
    @Json(name = "suggested_sort")
    Object suggestedSort();

    /**
     * #undocumented
     */
    @Json(name = "secure_media")
    Media secureMedia();

    /**
     * #undocumented
     */
    @Json(name = "from_kind")
    Object fromKind();

    /**
     * #undocumented
     */
    Preview preview();

    /**
     * #undocumented
     */
    @Json(name = "secure_media_embed")
    MediaEmbed secureMediaEmbed();

    /**
     * Returns a string that suggests the content of this link. As a hint, this is lossy and may be
     * inaccurate in some cases. If the submission is a Comment, then {@link PostHint#SELF} is
     * returned.
     * <p>
     * #inferred ({@code "post_hint"} defined at <a href="https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896">https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896</a>
     */
    @Json(name = "post_hint")
    PostHint postHint();

    /**
     * #undocumented
     */
    Object from();

    /**
     * #undocumented
     */
    Object fromId();

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
    String author();

    public abstract String getLinkAuthor();

    /**
     * The CSS class of the author's flair. subreddit specific.
     */
    @Json(name = "author_flair_css_class")
    String authorFlairCssClass();

    /**
     * The text of the author's flair. subreddit specific.
     */
    @Json(name = "author_flair_text")
    String authorFlairText();

    /**
     * Url of the permanent link.
     */
    public abstract String getPermalink();

    /**
     * {@code true} if this post is saved by the logged in user.
     */
    boolean saved();

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
    int score();

    /**
     * Subreddit of thing excluding the /r/ prefix e.g. "pics".
     */
    String subreddit();

    /**
     * The id of the subreddit in which the thing is located.
     */
    @Json(name = "subreddit_id")
    String subredditId();

    /**
     * Indicates if link has been edited. Will be the edit timestamp if the link has been edited
     * and return {@code false} otherwise.
     */
    Object edited();

    /**
     * To allow determining whether they have been distinguished by moderators/admins. If
     * {@code null} then not distinguished.
     */
    Distinguished distinguished();

    /**
     * {@code true} if the post is set as the sticky in its subreddit.
     */
    boolean stickied();

    /**
     * Who removed this submission. {@code null} if nobody or you are not a mod.
     */
    @Json(name = "banned_by")
    String bannedBy();

    /**
     * #undocumented
     */
    @Json(name = "user_reports")
    List<Object> userReports();

    /**
     * #undocumented
     */
    String id();

    /**
     * ID of the link the submission is or is in.
     */
    public abstract String getLinkId();

    /**
     * The number of times this submission received Reddit Gold.
     */
    int gilded();

    /**
     * Is this link archived.
     * <p>
     * #inferred
     */
    boolean archived();

    /**
     * #undocumented
     */
    @Json(name = "report_reasons")
    Object reportReasons();

    /**
     * Who approved this submission. {@code null} if nobody or you are not a mod.
     */
    @Json(name = "approved_by")
    String approvedBy();

    /**
     * #undocumented
     */
    @Json(name = "removal_reason")
    Object removalReason();

    @Json(name = "name")
    String fullname();

    /**
     * #undocumented
     */
    @Json(name = "mod_reports")
    List<Object> modReports();

    /**
     * How many times this submission has been reported, {@code null} if not a mod.
     */
    @Json(name = "num_reports")
    Object numReports();

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
