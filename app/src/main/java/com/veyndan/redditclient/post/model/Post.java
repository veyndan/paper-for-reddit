package com.veyndan.redditclient.post.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.veyndan.redditclient.R;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.More;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.api.reddit.model.Preview;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.network.VoteDirection;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.util.Node;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;

public class Post extends Node<Response<Thing<Listing>>> {

    private Observable<?> mediaObservable = Observable.empty();

    private final boolean isLink;
    private final boolean isComment;

    private final List<Node<Response<Thing<Listing>>>> replies;

    private final boolean archived;
    private final String author;
    private final String bodyHtml;
    private final int commentCount;
    private final long createdUtc;
    private final String domain;
    private final String fullname;
    private final int gildedCount;
    private VoteDirection likes;
    private final boolean hasLinkFlair; // TODO Probably don't want this.
    private final String linkFlair;
    private final String linkTitle;
    private String linkUrl;
    private final boolean nsfw;
    private final String permalink;
    private int points;
    private PostHint postHint;
    private final Preview preview;
    private boolean saved;
    private final boolean scoreHidden;
    private final boolean stickied;
    private final String subreddit;

    public Post(@NonNull final Submission submission) {
        isLink = submission instanceof Link;
        isComment = submission instanceof Comment;

        replies = new ArrayList<>();
        for (final RedditObject child : submission.getReplies().data.children) {
            if (child instanceof Submission) {
                Observable.just(child)
                        .cast(Submission.class)
                        .map(Post::new)
                        .flatMap(Mutators.mutate())
                        .subscribe(replies::add);
            } else if (child instanceof More) {
                final More more = (More) child;
                replies.add(new Progress.Builder()
                        .trigger(Observable.empty())
                        .childCount(more.count)
                        .build());
            } else {
                throw new IllegalStateException("Unknown node class: " + child);
            }
        }

        archived = submission.archived;
        author = submission.author == null ? "" : submission.author;
        bodyHtml = submission.bodyHtml;
        commentCount = submission.getNumComments();
        createdUtc = submission.createdUtc;
        domain = submission.getDomain();
        fullname = submission.getFullname();
        gildedCount = submission.gilded;
        likes = submission.getLikes();
        hasLinkFlair = isLink && !TextUtils.isEmpty(submission.getLinkFlairText());
        linkFlair = hasLinkFlair ? submission.getLinkFlairText() : null;
        linkTitle = submission.linkTitle;
        linkUrl = submission.linkUrl == null ? "" : submission.linkUrl;
        nsfw = submission.isOver18();
        permalink = submission.getPermalink();
        points = submission.score;

        postHint = submission.getPostHint();

        preview = submission.getPreview();
        saved = submission.saved;
        scoreHidden = submission.scoreHidden;
        stickied = submission.stickied;
        subreddit = submission.subreddit;
    }

    public Observable<?> getMediaObservable() {
        return mediaObservable;
    }

    public void setMediaObservable(final Observable<?> mediaObservable) {
        this.mediaObservable = mediaObservable;
    }

    public boolean isLink() {
        return isLink;
    }

    public boolean isComment() {
        return isComment;
    }

    public boolean isArchived() {
        return archived;
    }

    public String getAuthor() {
        return author;
    }

    public String getDomain() {
        return domain;
    }

    public String getFullname() {
        return fullname;
    }

    public boolean isGilded() {
        return gildedCount > 0;
    }

    public int getGildedCount() {
        return gildedCount;
    }

    public VoteDirection getLikes() {
        return likes;
    }

    public void setLikes(final VoteDirection likes) {
        this.likes = likes;
    }

    public boolean hasLinkFlair() {
        return hasLinkFlair;
    }

    public String getLinkFlair() {
        return linkFlair;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public String getPermalink() {
        return permalink;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public PostHint getPostHint() {
        return postHint;
    }

    public void setPostHint(@NonNull final PostHint postHint) {
        this.postHint = postHint;
    }

    public Preview getPreview() {
        return preview;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(final boolean saved) {
        this.saved = saved;
    }

    public boolean isStickied() {
        return stickied;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setLinkUrl(final String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public CharSequence getDisplayAge() {
        return DateUtils.getRelativeTimeSpanString(
                TimeUnit.SECONDS.toMillis(createdUtc), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_NOON
                        | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_NO_MONTH_DAY);
    }

    @Nullable
    public CharSequence getDisplayBody() {
        if (TextUtils.isEmpty(bodyHtml)) return null;
        return trimTrailingWhitespace(Html.fromHtml(StringEscapeUtils.unescapeHtml4(bodyHtml)));
    }

    private static CharSequence trimTrailingWhitespace(@NonNull final CharSequence source) {
        int i = source.length();

        // loop back to the first non-whitespace character
        do {
            i--;
        } while (i > 0 && Character.isWhitespace(source.charAt(i)));

        return source.subSequence(0, i + 1);
    }

    public String getDisplayComments(final Context context) {
        final Resources resources = context.getResources();
        return resources.getQuantityString(R.plurals.comments, commentCount, commentCount);
    }

    public String getDisplayPoints(final Context context, final String scoreHiddenText) {
        if (scoreHidden) {
            return scoreHiddenText;
        } else {
            final Resources resources = context.getResources();
            return resources.getQuantityString(R.plurals.points, points, points);
        }
    }

    @NonNull
    @Override
    public List<Node<Response<Thing<Listing>>>> getChildren() {
        return replies;
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> asObservable() {
        return Observable.empty();
    }
}
