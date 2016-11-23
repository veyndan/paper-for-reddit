package com.veyndan.paper.reddit.post.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.veyndan.paper.reddit.util.Linkifier;
import com.veyndan.paper.reddit.util.Node;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Response;

public class Post extends Node<Response<Thing<Listing>>> {

    private final List<Object> medias = new ArrayList<>();

    private final boolean isLink;
    private final boolean isComment;

    private final Observable<Node<Response<Thing<Listing>>>> children;
    private boolean descendantsVisible;

    private final boolean archived;
    private final String bodyHtml;
    private final String domain;
    private final String fullname;
    private final boolean hideable;
    private VoteDirection likes;
    private String linkUrl;
    private final String permalink;
    private int points;
    private PostHint postHint;
    private final Preview preview;
    private boolean saved;
    private final boolean scoreHidden;

    public static List<?> create(final Submission submission) {
        final String author = submission.author == null ? "" : submission.author;
        final long createdUtc = submission.createdUtc;
        final int gildedCount = submission.gilded;
        final boolean stickied = submission.stickied;
        final String linkFlair = submission.getLinkFlairText();
        final String linkTitle = submission.linkTitle;
        final boolean nsfw = submission.isOver18();
        final String subreddit = submission.subreddit;

        final Title title = new Title(author, createdUtc, gildedCount, linkFlair, linkTitle, nsfw,
                stickied, subreddit);

        final List<Object> particles = new ArrayList<>();
        particles.add(title);
        return particles;
    }

    public static final class Title {

        private final String author;
        private final long createdUtc;
        private final int gildedCount;
        private final String linkFlair;
        private final String linkTitle;
        private final boolean nsfw;
        private final boolean stickied;
        private final String subreddit;

        private Title(final String author, final long createdUtc, final int gildedCount,
                      final String linkFlair, final String linkTitle, final boolean nsfw,
                      final boolean stickied, final String subreddit) {
            this.author = author;
            this.createdUtc = createdUtc;
            this.gildedCount = gildedCount;
            this.linkFlair = linkFlair;
            this.linkTitle = linkTitle;
            this.nsfw = nsfw;
            this.stickied = stickied;
            this.subreddit = subreddit;
        }

        public String getAuthor() {
            return author;
        }

        public long getCreatedUtc() {
            return createdUtc;
        }

        public boolean isGilded() {
            return gildedCount > 0;
        }

        public int getGildedCount() {
            return gildedCount;
        }

        public boolean hasLinkFlair() {
            return !TextUtils.isEmpty(linkFlair);
        }

        public String getLinkFlair() {
            return linkFlair;
        }

        public String getLinkTitle() {
            return linkTitle;
        }

        public boolean isNsfw() {
            return nsfw;
        }

        public boolean isStickied() {
            return stickied;
        }

        public String getSubreddit() {
            return subreddit;
        }

        public CharSequence getDisplayAge() {
            return DateUtils.getRelativeTimeSpanString(
                    TimeUnit.SECONDS.toMillis(createdUtc), System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_NOON
                            | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_NO_MONTH_DAY);
        }
    }

    public Post(@NonNull final Submission submission) {
        isLink = submission instanceof Link;
        isComment = submission instanceof Comment;

        children = Observable.fromIterable(submission.getReplies().data.children)
                .concatMap(redditObject -> {
                    if (redditObject instanceof Submission) {
                        return Single.just(redditObject)
                                .cast(Submission.class)
                                .map(Post::new)
                                .flatMap(Mutators.mutate())
                                .toObservable();
                    } else if (redditObject instanceof More) {
                        return Single.just(redditObject)
                                .cast(More.class)
                                .map(more -> new Progress.Builder()
                                        .trigger(Observable.just(true))
                                        .degree(more.count)
                                        .build())
                                .toObservable();
                    } else {
                        return Observable.error(new IllegalStateException("Unknown node class: " + redditObject));
                    }
                });
        if (isLink) {
            setDescendantCount(submission.getNumComments());
        }
        descendantsVisible = isComment;

        archived = submission.archived;
        bodyHtml = submission.bodyHtml;
        domain = submission.getDomain();
        fullname = submission.getFullname();
        hideable = isLink;
        likes = submission.getLikes();
        linkUrl = submission.linkUrl == null ? "" : submission.linkUrl;
        permalink = submission.getPermalink();
        points = submission.score;

        postHint = submission.getPostHint();

        preview = submission.getPreview();
        saved = submission.saved;
        scoreHidden = submission.scoreHidden;
    }

    public List<Object> getMedias() {
        return medias;
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

    public String getBody() {
        return bodyHtml;
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

    public boolean isHideable() {
        return hideable;
    }

    public VoteDirection getLikes() {
        return likes;
    }

    public void setLikes(final VoteDirection likes) {
        this.likes = likes;
    }

    public String getLinkUrl() {
        return linkUrl;
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

    public void setLinkUrl(final String linkUrl) {
        this.linkUrl = linkUrl;
    }

    @Nullable
    public Spannable getDisplayBody(final Context context) {
        if (TextUtils.isEmpty(bodyHtml)) {
            return null;
        }
        final Spannable html = new SpannableString(trimTrailingWhitespace(Html.fromHtml(StringEscapeUtils.unescapeHtml4(bodyHtml))));
        Linkifier.addLinks(context, html);
        return html;
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
        return getDescendantCount() > 0;
    }

    public boolean isDescendantsVisible() {
        return descendantsVisible;
    }

    public void setDescendantsVisible(final boolean descendantsVisible) {
        this.descendantsVisible = descendantsVisible;
    }

    public String getDisplayDescendants() {
        final int descendantCount = getDescendantCount();
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
