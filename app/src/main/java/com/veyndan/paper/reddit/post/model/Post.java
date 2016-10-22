package com.veyndan.paper.reddit.post.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.model.Comment;
import com.veyndan.paper.reddit.api.reddit.model.Link;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.More;
import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.api.reddit.model.Preview;
import com.veyndan.paper.reddit.api.reddit.model.Submission;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;
import com.veyndan.paper.reddit.post.media.mutator.Mutators;
import com.veyndan.paper.reddit.util.Node;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;

public class Post extends Node<Response<Thing<Listing>>> {

    private Observable<?> mediaObservable = Observable.empty();

    private final boolean isLink;
    private final boolean isComment;

    private final Observable<Node<Response<Thing<Listing>>>> children;
    private final int descendantCount;

    private final boolean archived;
    private final String author;
    private final String bodyHtml;
    private final long createdUtc;
    private final String domain;
    private final String fullname;
    private final int gildedCount;
    private final boolean hideable;
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

        children = Observable.from(submission.getReplies().data.children)
                .concatMap(redditObject -> {
                    if (redditObject instanceof Submission) {
                        return Observable.just(redditObject)
                                .cast(Submission.class)
                                .map(Post::new)
                                .flatMap(Mutators.mutate());
                    } else if (redditObject instanceof More) {
                        return Observable.just(redditObject)
                                .cast(More.class)
                                .map(more -> new Progress.Builder()
                                        .trigger(Observable.just(true))
                                        .degree(more.count)
                                        .build());
                    } else {
                        return Observable.error(new IllegalStateException("Unknown node class: " + redditObject));
                    }
                });
        descendantCount = submission.getNumComments();

        archived = submission.archived;
        author = submission.author == null ? "" : submission.author;
        bodyHtml = submission.bodyHtml;
        createdUtc = submission.createdUtc;
        domain = submission.getDomain();
        fullname = submission.getFullname();
        gildedCount = submission.gilded;
        hideable = isLink;
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

    public String getArticle() {
        return fullname.substring(3, fullname.length());
    }

    public boolean isGilded() {
        return gildedCount > 0;
    }

    public int getGildedCount() {
        return gildedCount;
    }

    public boolean isHideable() {
        return hideable;
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
        if (TextUtils.isEmpty(bodyHtml)) {
            return null;
        }
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

    public boolean isInternalNode() {
        return descendantCount > 0;
    }

    public String getDisplayDescendants() {
        if (descendantCount < 1000) {
            return String.valueOf(descendantCount);
        } else if (descendantCount < 100000) {
            final int beforeDecimal = descendantCount / 1000;
            final int afterDecimal = descendantCount % 1000 / 100;

            final int maxStringSize = 5; // e.g. "99.9K"
            final StringBuilder result = new StringBuilder(maxStringSize);
            result.append(beforeDecimal);
            if (afterDecimal > 0) {
                result.append('.').append(afterDecimal);
            }
            result.append('K');
            return result.toString();
        } else {
            throw new UnsupportedOperationException("Descendant count summarization not implemented yet for: " + descendantCount);
        }
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
    public Observable<Node<Response<Thing<Listing>>>> getChildren() {
        return children;
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> asObservable() {
        return Observable.empty();
    }
}
