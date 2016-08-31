package com.veyndan.redditclient.post.model;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.veyndan.redditclient.R;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.api.reddit.model.Preview;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.api.reddit.network.VoteDirection;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class Post {

    private final Submission submission;

    private Observable<Object> mediaObservable = Observable.empty();

    public Post(final RedditObject redditObject) {
        this.submission = (Submission) redditObject;
    }

    public Observable<Object> getMediaObservable() {
        return mediaObservable;
    }

    public <T> void setMediaObservable(final Observable<T> mediaObservable) {
        this.mediaObservable = mediaObservable.cast(Object.class);
    }

    public boolean isLink() {
        return submission instanceof Link;
    }

    public boolean isComment() {
        return submission instanceof Comment;
    }

    public String getLinkTitle() {
        return submission.linkTitle;
    }

    public String getLinkUrl() {
        return submission.linkUrl;
    }

    public void setLinkUrl(final String linkUrl) {
        submission.linkUrl = linkUrl;
    }

    public String getAuthor() {
        return submission.author;
    }

    public CharSequence getDisplayAge() {
        return DateUtils.getRelativeTimeSpanString(
                TimeUnit.SECONDS.toMillis(submission.createdUtc), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_NOON
                        | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_NO_MONTH_DAY);
    }

    public String getSubreddit() {
        return submission.subreddit;
    }

    public boolean isStickied() {
        return submission.stickied;
    }

    public boolean isNsfw() {
        return submission instanceof Link && ((Link) submission).over18;
    }

    public boolean hasLinkFlair() {
        return submission instanceof Link && !TextUtils.isEmpty(((Link) submission).linkFlairText);
    }

    public String getLinkFlair() {
        return hasLinkFlair() ? ((Link) submission).linkFlairText : null;
    }

    public boolean isGilded() {
        return submission.gilded > 0;
    }

    public int getGildedCount() {
        return submission.gilded;
    }

    public int getPoints() {
        return submission.score;
    }

    public void setPoints(final int points) {
        submission.score = points;
    }

    public String getDisplayPoints(final Context context, final String scoreHiddenText) {
        if (submission.scoreHidden) {
            return scoreHiddenText;
        } else {
            final Resources resources = context.getResources();
            final int points = submission.score;
            return resources.getQuantityString(R.plurals.points, points, points);
        }
    }

    public String getDisplayComments(final Context context) {
        final Resources resources = context.getResources();
        final int commentCount = submission instanceof Link ? ((Link) submission).numComments : 0;
        return resources.getQuantityString(R.plurals.comments, commentCount, commentCount);
    }

    public VoteDirection getLikes() {
        return submission.getLikes();
    }

    public void setLikes(final VoteDirection voteDirection) {
        submission.setLikes(voteDirection);
    }

    public boolean isArchived() {
        return submission.archived;
    }

    public String getFullname() {
        return submission.getFullname();
    }

    public boolean isSaved() {
        return submission.saved;
    }

    public void setSaved(final boolean saved) {
        submission.saved = saved;
    }

    public boolean getPermalink() {
        return submission.saved;
    }

    public String getBodyHtml() {
        return submission.bodyHtml;
    }

    public PostHint getPostHint() {
        return submission instanceof Link ? ((Link) submission).getPostHint() : null;
    }

    public void setPostHint(final PostHint postHint) {
        if (submission instanceof Link) {
            ((Link) submission).setPostHint(postHint);
        }
    }

    public Preview getPreview() {
        return submission instanceof Link ? ((Link) submission).preview : null;
    }

    public String getDomain() {
        return submission instanceof Link ? ((Link) submission).domain : null;
    }
}
