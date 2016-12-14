package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Maybe;

// TODO Votable and Created should be super classes some how.
public abstract class Submission extends RedditObject {

    // TODO: Make a Body class which encapsulates post hint, media and body text so Comment can have the same with a constant post hint of SELF.
    // TODO: which can be defined in Submission.java.
    // TODO: Could have custom post hints? E.g. Tweets for the converter adapters. Hard part is where to define the attributes for each converter e.g. Tweet id. Could have hash map or something.
    // Votable
    private int ups;
    private int downs;

    public int getUps() {
        return ups;
    }

    public int getDowns() {
        return downs;
    }

    /**
     * How the logged-in user has voted on the submission. {@code true} = upvoted,
     * {@code false} = downvoted, {@code null} = no vote.
     */
    @Nullable private Boolean likes;

    @NonNull
    public VoteDirection getLikes() {
        if (likes == null) {
            return VoteDirection.UNVOTE;
        }
        return likes ? VoteDirection.UPVOTE : VoteDirection.DOWNVOTE;
    }

    // Created
    private long created;
    private long createdUtc;

    public long getCreated() {
        return created;
    }

    public long getCreatedUtc() {
        return createdUtc;
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
    @NonNull
    public abstract Maybe<String> getDomain();

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
    @NonNull
    public abstract Maybe<String> getLinkFlairCssClass();

    /**
     * The text of the link's flair. If the submission is a Comment, then {@code null} is returned.
     */
    @NonNull
    public abstract Maybe<String> getLinkFlairText();

    /**
     * Whether the submission is locked (closed to new comments) or not. If the submission is a
     * comment, then {@code false} is returned.
     */
    public abstract boolean isLocked();

    /**
     * Used for streaming video. Detailed information about the video and it's origins are placed
     * here. If the submission is a Comment, then {@code null} is returned.
     */
    @NonNull
    public abstract Maybe<Media> getMedia();

    /**
     * Used for streaming video. Technical embed specific information is found here. If the
     * submission is a Comment, then {@code null} is returned.
     */
    @NonNull
    public abstract Maybe<MediaEmbed> getMediaEmbed();

    /**
     * The number of comments that belong to this link. includes removed comments. If the submission
     * is a Comment, then {@code 0} is returned.
     */
    @NonNull
    public abstract Maybe<Integer> getNumComments();

    /**
     * {@code true} if the post is tagged as NSFW. {@code false} if otherwise. If the submission is
     * a Comment, then {@code false} is returned.
     */
    public abstract boolean isOver18();

    /**
     * Full URL to the thumbnail for this link. "self" if this is a self post. "default" if a
     * thumbnail is not available. If the submission is a Comment, then {@code null} is returned.
     */
    @NonNull
    public abstract Maybe<String> getThumbnail();

    /**
     * #undocumented
     */
    @NonNull
    public abstract Maybe<Object> getSuggestedSort();

    /**
     * #undocumented
     */
    @NonNull
    public abstract Maybe<Media> getSecureMedia();

    /**
     * #undocumented
     */
    @Nullable
    public abstract Object getFromKind();

    /**
     * #undocumented
     */
    @NonNull
    public abstract Preview getPreview();

    /**
     * #undocumented
     */
    @NonNull
    public abstract Maybe<MediaEmbed> getSecureMediaEmbed();

    /**
     * Returns a string that suggests the content of this link. As a hint, this is lossy and may be
     * inaccurate in some cases. If the submission is a Comment, then {@link PostHint#SELF} is
     * returned.
     * <p>
     * #inferred ({@code "post_hint"} defined at <a href="https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896">https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896</a>
     */
    @NonNull
    public abstract PostHint getPostHint();

    /**
     * #undocumented
     */
    @Nullable
    public abstract Object from();

    /**
     * #undocumented
     */
    @Nullable
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
    @NonNull
    public abstract Maybe<String> getParentId();

    /**
     * The comment nodes/leaves exactly one below the current Submission.
     */
    @NonNull
    public abstract Thing<Listing> getReplies();

    @Nullable private String author;

    /**
     * The account name of the poster. {@code null} if this is a promotional link.
     */
    @NonNull
    public String getAuthor() {
        return Objects.requireNonNull(author);
    }

    @Nullable
    public abstract String getLinkAuthor();

    @Nullable private String authorFlairCssClass;

    /**
     * The CSS class of the author's flair. subreddit specific.
     */
    @NonNull
    public String getAuthorFlairCssClass() {
        return Objects.requireNonNull(authorFlairCssClass);
    }

    @Nullable private String authorFlairText;

    /**
     * The text of the author's flair. subreddit specific.
     */
    @NonNull
    public String getAuthorFlairText() {
        return Objects.requireNonNull(authorFlairText);
    }

    /**
     * Url of the permanent link.
     */
    @NonNull
    public abstract String getPermalink();

    private boolean saved;

    /**
     * {@code true} if this post is saved by the logged in user.
     */
    public boolean isSaved() {
        return saved;
    }

    private int score;

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
    public int getScore() {
        return score;
    }

    @Nullable private String subreddit;

    /**
     * Subreddit of thing excluding the /r/ prefix e.g. "pics".
     */
    @NonNull
    public String getSubreddit() {
        return Objects.requireNonNull(subreddit);
    }

    @Nullable private String subredditId;

    /**
     * The id of the subreddit in which the thing is located.
     */
    @NonNull
    public String getSubredditId() {
        return Objects.requireNonNull(subredditId);
    }

    @Nullable private Object edited;

    /**
     * Indicates if link has been edited. Will be the edit timestamp if the link has been edited
     * and return {@code false} otherwise.
     */
    @NonNull
    public Object getEdited() {
        return Objects.requireNonNull(edited);
    }

    @Nullable private Distinguished distinguished;

    /**
     * To allow determining whether they have been distinguished by moderators/admins. If
     * {@code null} then not distinguished.
     */
    @NonNull
    public Distinguished getDistinguished() {
        return Objects.requireNonNull(distinguished);
    }

    private boolean stickied;

    /**
     * {@code true} if the post is set as the sticky in its subreddit.
     */
    public boolean isStickied() {
        return stickied;
    }

    @Nullable private String bannedBy;

    /**
     * Who removed this submission. {@code null} if nobody or you are not a mod.
     */
    @NonNull
    public String getBannedBy() {
        return Objects.requireNonNull(bannedBy);
    }

    @NonNull private List<Object> userReports = new ArrayList<>();

    /**
     * #undocumented
     */
    @NonNull
    public List<Object> getUserReports() {
        return userReports;
    }

    @Nullable private String id;

    /**
     * #undocumented
     */
    @NonNull
    public String getId() {
        return Objects.requireNonNull(id);
    }

    /**
     * ID of the link the submission is or is in.
     */
    @Nullable
    public abstract String getLinkId();

    private int gilded;

    /**
     * The number of times this submission received Reddit Gold.
     */
    public int getGilded() {
        return gilded;
    }

    private boolean archived;

    /**
     * Is this link archived.
     * <p>
     * #inferred
     */
    public boolean isArchived() {
        return archived;
    }

    @Nullable private Object reportReasons;

    /**
     * #undocumented
     */
    @NonNull
    public Object getReportReasons() {
        return Objects.requireNonNull(reportReasons);
    }

    @Nullable private String approvedBy;

    /**
     * Who approved this submission. {@code null} if nobody or you are not a mod.
     */
    @NonNull
    public String getApprovedBy() {
        return Objects.requireNonNull(approvedBy);
    }

    @Nullable private Object removalReason;

    /**
     * #undocumented
     */
    @NonNull
    public Object getRemovalReason() {
        return Objects.requireNonNull(removalReason);
    }

    @Nullable private String name;

    /**
     * #undocumented
     */
    @Nullable
    public String getFullname() {
        return name;
    }

    @NonNull private List<Object> modReports = new ArrayList<>();

    /**
     * #undocumented
     */
    @NonNull
    public List<Object> getModReports() {
        return modReports;
    }

    @Nullable private Object numReports;

    /**
     * How many times this submission has been reported, {@code null} if not a mod.
     */
    @NonNull
    public Object getNumReports() {
        return Objects.requireNonNull(numReports);
    }

    @SerializedName(value = "score_hidden", alternate = "hide_score") private boolean scoreHidden;

    /**
     * Should the score be hidden.
     * <p>
     * #inferred
     */
    public boolean isScoreHidden() {
        return scoreHidden;
    }

    @NonNull @SerializedName(value = "link_title", alternate = "title") private String linkTitle = "";

    /**
     * The title of the link. May contain newlines for some reason.
     */
    @NonNull
    public String getLinkTitle() {
        return linkTitle;
    }

    @NonNull @SerializedName(value = "link_url", alternate = "url") private String linkUrl = "";

    /**
     * The link of this post. The permalink if this is a self-post.
     */
    @NonNull
    public String getLinkUrl() {
        return linkUrl;
    }

    @Nullable @SerializedName(value = "body", alternate = "selftext") private String body;

    /**
     * The raw text. This is the unformatted text which includes the raw markup characters such as
     * ** for bold. <, >, and & are escaped.
     * <p>
     * If it is a {@link Link} then this is the self text if available. Empty if not present.
     */
    @NonNull
    public String getBody() {
        return Objects.requireNonNull(body);
    }

    @Nullable @SerializedName(value = "body_html", alternate = "selftext_html") private final String bodyHtml = "";

    /**
     * The formatted HTML text as displayed on reddit. For example, text that is emphasised by *
     * will now have <em> tags wrapping it. Additionally, bullets and numbered lists will now be in
     * HTML list format. NOTE: The HTML string will be escaped. You must unescape to get the raw
     * HTML.
     * <p>
     * If it is a {@link Link} then this is the self text if available. Empty string if not present.
     */
    @NonNull
    public String getBodyHtml() {
        return Objects.requireNonNull(bodyHtml);
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
