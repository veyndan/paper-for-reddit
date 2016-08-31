package com.veyndan.redditclient.api.reddit.model;

import com.google.common.collect.ImmutableList;

import java.util.regex.Pattern;

import okhttp3.HttpUrl;

public class Link extends Submission {

    private static final ImmutableList<String> DIRECT_IMAGE_DOMAINS = ImmutableList.of(
            "i.imgur.com", "i.redd.it", "i.reddituploads.com", "pbs.twimg.com",
            "upload.wikimedia.org");

    /**
     * Probably always returns false.
     * <p>
     * #undocumented
     */
    public boolean clicked;

    /**
     * The domain of this link. {@code self} posts will be {@code self.<subreddit>} while other
     * examples include {@code en.wikipedia.org} and {@code s3.amazon.com}.
     */
    public String domain;

    /**
     * {@code true} if the post is hidden by the logged in user. {@code false} if not logged in or
     * not hidden.
     */
    public boolean hidden;

    /**
     * {@code true} if this link is a selfpost.
     */
    private boolean isSelf;

    /**
     * The CSS class of the link's flair.
     */
    public String linkFlairCssClass;

    /**
     * The text of the link's flair.
     */
    public String linkFlairText;

    /**
     * Whether the link is locked (closed to new comments) or not.
     */
    public boolean locked;

    /**
     * Used for streaming video. Detailed information about the video and it's origins are placed
     * here.
     */
    public Media media;

    /**
     * Used for streaming video. Technical embed specific information is found here.
     */
    public MediaEmbed mediaEmbed;

    /**
     * The number of comments that belong to this link. includes removed comments.
     */
    public int numComments;

    /**
     * {@code true} if the post is tagged as NSFW. {@code false} if otherwise.
     */
    public boolean over18;

    private String permalink;

    @Override
    public String getLinkAuthor() {
        return author;
    }

    @Override
    public String getPermalink() {
        return "https://www.reddit.com" + permalink;
    }

    @Override
    public String getLinkId() {
        return id;
    }

    /**
     * Full URL to the thumbnail for this link. "self" if this is a self post. "default" if a
     * thumbnail is not available.
     */
    public String thumbnail;

    /**
     * #undocumented
     */
    public Object suggestedSort;

    /**
     * #undocumented
     */
    public Media secureMedia;

    /**
     * #undocumented
     */
    public Object fromKind;

    /**
     * #undocumented
     */
    public final Preview preview = new Preview();

    /**
     * #undocumented
     */
    public MediaEmbed secureMediaEmbed;

    private PostHint postHint = PostHint.LINK;

    /**
     * Returns a string that suggests the content of this link.
     * As a hint, this is lossy and may be inaccurate in some cases.
     * <p>
     * #inferred ({@code "post_hint"} defined at <a href="https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896">https://github.com/reddit/reddit/blob/b423fe2bf873919b27c4eab885551c8ee325b9af/r2/r2/models/link.py#L896</a>
     */
    public PostHint getPostHint() {
        if (isSelf) {
            postHint = PostHint.SELF;
        } else if (Pattern.compile("(.jpg|.jpeg|.gif|.png)$").matcher(linkUrl).find()
                || DIRECT_IMAGE_DOMAINS.contains(HttpUrl.parse(linkUrl).host())) {
            postHint = PostHint.IMAGE;
        }
        return postHint;
    }

    /**
     * #undocumented
     */
    public Object from;

    /**
     * #undocumented
     */
    public Object fromId;

    /**
     * #undocumented
     */
    public boolean quarantine;

    /**
     * #undocumented
     */
    public boolean visited;
}
