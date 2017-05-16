package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;

import java.util.regex.Pattern;

import okhttp3.HttpUrl;

// TODO Just reset hard tbh. Note how the Submission is an interface and how you can annotate
// the methods in the interface with @Json and they propagate to the implementers. Take a look
// at the interfaces Votable and Created as they do the same thing. It's very clean and nice to do
// it like this. Note that you should reset hard as there are going to be obvious problems as no
// default values are used. Also there are weird problems that should be solved before converting this
// to AutoValue classes e.g. postHint() is kind of odd as it mutates the state of postHint. Also,
// take note in Thing.java and Basic.java how the JsonAdapter factory is made. Also, don't reset
// the entire branch as the Reddit and Imgur are done correctly. Look at the commit message for Imgur
// conversion in the current branch as it states why you shouldn't default to an empty list but instead
// just fail. Alternatively, just pass in an empty list just in case as if it throws an exception, I'll
// have to do it anyway as I don't have control over the Imgur API. You also want to see if you can omit
// fields in json deserialization using auto-value-moshi for certain cases i.e. https://github.com/rharter/auto-value-moshi/issues/55
// Also default values can just be set through a custom JsonAdapter i.e. DefaultOnNullAdapter.

@AutoValue
public abstract class Link implements Submission {

    private static final ImmutableList<String> DIRECT_IMAGE_DOMAINS = ImmutableList.of(
            "i.imgur.com", "i.redd.it", "i.reddituploads.com", "pbs.twimg.com",
            "upload.wikimedia.org");

    @Override
    public VoteDirection voteDirection() {
        if (likes() == null) {
            return VoteDirection.UNVOTE;
        }
        return likes() ? VoteDirection.UPVOTE : VoteDirection.DOWNVOTE;
    }

    @Override
    public PostHint postHint() {
        final String linkUrl = getLinkUrl();
        if (isSelf) {
            postHint = PostHint.SELF;
        } else if (Pattern.compile("(.jpg|.jpeg|.gif|.png)$").matcher(linkUrl).find()
                || DIRECT_IMAGE_DOMAINS.contains(HttpUrl.parse(linkUrl).host())) {
            postHint =  PostHint.IMAGE;
        }
        return postHint;
    }

    //

    @Json(name = "is_self") private boolean isSelf;
    private String permalink;
    private Object from;
    @Json(name = "from_id") private Object fromId;
    private boolean quarantine;
    private boolean visited;
    private Thing<Listing> replies = Thing.create(Listing.create());

    @Override
    public Object from() {
        return from;
    }

    @Override
    public Object fromId() {
        return fromId;
    }

    @Override
    public boolean quarantine() {
        return quarantine;
    }

    @Override
    public boolean visited() {
        return visited;
    }

    @Override
    public String getParentId() {
        return null;
    }

    @Override
    public Thing<Listing> getReplies() {
        return replies;
    }

    @Override
    public String getLinkAuthor() {
        return author();
    }

    @Override
    public String getPermalink() {
        return "https://www.reddit.com" + permalink;
    }

    @Override
    public String getLinkId() {
        return id();
    }

    @Override
    public int getControversiality() {
        throw new UnsupportedOperationException("Method intention unknown");
    }

    @Override
    public boolean isHideable() {
        return true;
    }

    public static JsonAdapter<Link> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Link.MoshiJsonAdapter(moshi);
    }
}
