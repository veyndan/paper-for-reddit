package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.squareup.moshi.Json;

import java.util.regex.Pattern;

import okhttp3.HttpUrl;

public class Link extends Submission {

    private static final ImmutableList<String> DIRECT_IMAGE_DOMAINS = ImmutableList.of(
            "i.imgur.com", "i.redd.it", "i.reddituploads.com", "pbs.twimg.com",
            "upload.wikimedia.org");

    private String domain;
    @Json(name = "is_self") private boolean isSelf;
    @Json(name = "link_flair_text") private String linkFlairText;
    private boolean locked;
    private Media media;
    @Json(name = "num_comments") private int numComments;
    @Json(name = "over_18") private boolean over18;
    private String permalink;
    private final Preview preview = new Preview();
    @Json(name = "post_hint") private PostHint postHint = PostHint.LINK;
    private Thing<Listing> replies = new Thing<>(new Listing());

    @Override
    public PostHint getPostHint() {
        final String linkUrl = getLinkUrl();
        if (isSelf) {
            postHint = PostHint.SELF;
        } else if (Pattern.compile("(.jpg|.jpeg|.gif|.png)$").matcher(linkUrl).find()
                || DIRECT_IMAGE_DOMAINS.contains(HttpUrl.parse(linkUrl).host())) {
            postHint = PostHint.IMAGE;
        }
        return postHint;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getLinkFlairText() {
        return linkFlairText;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public Media getMedia() {
        return media;
    }

    @Nullable
    @IntRange(from = 0)
    @Override
    public Integer getNumComments() {
        return numComments;
    }

    @Override
    public boolean isOver18() {
        return over18;
    }

    @Override
    public Preview getPreview() {
        return preview;
    }

    @Override
    public Thing<Listing> getReplies() {
        return replies;
    }

    @Override
    public String getPermalink() {
        return "https://www.reddit.com" + permalink;
    }

    @Override
    public boolean isHideable() {
        return true;
    }
}
