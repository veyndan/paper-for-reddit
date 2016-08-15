package com.veyndan.redditclient.api.reddit.model;

import com.google.gson.annotations.SerializedName;
import com.veyndan.redditclient.api.reddit.network.VoteDirection;

import java.util.ArrayList;
import java.util.List;

// TODO Votable and Created should be super classes some how.
public abstract class Submission extends RedditObject {

    // TODO: Make a Body class which encapsulates post hint, media and body text so Comment can have the same with a constant post hint of SELF.
    // TODO: which can be defined in Submission.java.
    // TODO: Could have custom post hints? E.g. Tweets for the converter adapters. Hard part is where to define the attributes for each converter e.g. Tweet id. Could have hash map or something.
    // Votable
    public int ups;
    public int downs;

    /**
     * How the logged-in user has voted on the submission. {@code true} = upvoted,
     * {@code false} = downvoted, {@code null} = no vote.
     */
    private Boolean likes;

    public VoteDirection getLikes() {
        if (likes == null) return VoteDirection.UNVOTE;
        return likes ? VoteDirection.UPVOTE : VoteDirection.DOWNVOTE;
    }

    public void setLikes(VoteDirection voteDirection) {
        switch (voteDirection) {
            case UPVOTE:
                likes = true;
                break;
            case DOWNVOTE:
                likes = false;
                break;
            case UNVOTE:
                likes = null;
                break;
            default:
                throw new IllegalStateException("Unknown vote direction");
        }
    }

    // Created
    public long created;
    public long createdUtc;

    /**
     * The account name of the poster. {@code null} if this is a promotional link.
     */
    public String author;

    public abstract String getLinkAuthor();

    /**
     * The CSS class of the author's flair. subreddit specific.
     */
    public String authorFlairCssClass;

    /**
     * The text of the author's flair. subreddit specific.
     */
    public String authorFlairText;

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
    public String subredditId;

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
    public String bannedBy;

    /**
     * #undocumented
     */
    public List<Object> userReports = new ArrayList<>();

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
    public Object reportReasons;

    /**
     * Who approved this submission. {@code null} if nobody or you are not a mod.
     */
    public String approvedBy;

    /**
     * #undocumented
     */
    public Object removalReason;

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
    public List<Object> modReports = new ArrayList<>();

    /**
     * How many times this submission has been reported, {@code null} if not a mod.
     */
    public Object numReports;

    /**
     * Should the score be hidden.
     * <p>
     * #inferred
     */
    @SerializedName(value = "score_hidden", alternate = "hide_score") public boolean scoreHidden;

    /**
     * The title of the link. May contain newlines for some reason.
     */
    @SerializedName(value = "link_title", alternate = "title") public String linkTitle;

    /**
     * The link of this post. The permalink if this is a self-post.
     */
    @SerializedName(value = "link_url", alternate = "url") public String linkUrl;

    /**
     * The raw text. This is the unformatted text which includes the raw markup characters such as
     * ** for bold. <, >, and & are escaped.
     * <p>
     * If it is a {@link Link} then this is the self text if available. Empty if not present.
     */
    @SerializedName(value = "body", alternate = "self_text") public String body;

    /**
     * The formatted HTML text as displayed on reddit. For example, text that is emphasised by *
     * will now have <em> tags wrapping it. Additionally, bullets and numbered lists will now be in
     * HTML list format. NOTE: The HTML string will be escaped. You must unescape to get the raw
     * HTML.
     * <p>
     * If it is a {@link Link} then this is the self text if available. {@code null} if not present.
     */
    @SerializedName(value = "body_html", alternate = "self_text_html") public String bodyHtml;
}
