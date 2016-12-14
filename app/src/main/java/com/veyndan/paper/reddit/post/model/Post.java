package com.veyndan.paper.reddit.post.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;

import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.model.Comment;
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

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Response;

public class Post extends Node<Response<Thing<Listing>>> {

    @NonNull private final List<Object> medias = new ArrayList<>();

    private final boolean isComment;

    @NonNull private final Observable<Node<Response<Thing<Listing>>>> children;
    private boolean descendantsVisible;

    private final boolean archived;
    @NonNull private final String author;
    @NonNull private final String bodyHtml;
    private final long createdUtc;
    @NonNull private final Maybe<String> domain;
    @Nullable private final String fullname;
    private final int gildedCount;
    private final boolean hideable;
    @NonNull private VoteDirection likes;
    @NonNull private final Maybe<String> linkFlair;
    @NonNull private final String linkTitle;
    @NonNull private String linkUrl;
    private final boolean nsfw;
    @NonNull private final String permalink;
    private int points;
    @NonNull private PostHint postHint;
    @NonNull private final Preview preview;
    private boolean saved;
    private final boolean scoreHidden;
    private final boolean stickied;
    @NonNull private final String subreddit;

    public Post(@NonNull final Submission submission) {
        isComment = submission instanceof Comment;

        children = Observable.fromIterable(submission.getReplies().getData().getChildren())
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
                                        .degree(more.getCount())
                                        .build())
                                .toObservable();
                    } else {
                        return Observable.error(new IllegalStateException("Unknown node class: " + redditObject));
                    }
                });
        setDescendantCount(submission.getNumComments());
        descendantsVisible = isComment;

        archived = submission.isArchived();
        author = submission.getAuthor();
        bodyHtml = submission.getBodyHtml();
        createdUtc = submission.getCreatedUtc();
        domain = submission.getDomain();
        fullname = submission.getFullname();
        gildedCount = submission.getGilded();
        hideable = submission.isHideable();
        likes = submission.getLikes();
        linkFlair = submission.getLinkFlairText().filter(String::isEmpty);
        linkTitle = submission.getLinkTitle();
        linkUrl = submission.getLinkUrl();
        nsfw = submission.isOver18();
        permalink = submission.getPermalink();
        points = submission.getScore();

        postHint = submission.getPostHint();

        preview = submission.getPreview();
        saved = submission.isSaved();
        scoreHidden = submission.isScoreHidden();
        stickied = submission.isStickied();
        subreddit = submission.getSubreddit();
    }

    @NonNull
    public List<Object> getMedias() {
        return medias;
    }

    public boolean isComment() {
        return isComment;
    }

    public boolean isArchived() {
        return archived;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    @NonNull
    public String getBody() {
        return bodyHtml;
    }

    @NonNull
    public Maybe<String> getDomain() {
        return domain;
    }

    @Nullable
    public String getFullname() {
        return fullname;
    }

    @NonNull
    public String getArticle() {
        if (fullname == null) {
            throw new IllegalStateException();
        } else {
            return fullname.substring(3, fullname.length());
        }
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

    @NonNull
    public VoteDirection getLikes() {
        return likes;
    }

    public void setLikes(@NonNull final VoteDirection likes) {
        this.likes = likes;
    }

    @NonNull
    public Maybe<String> getLinkFlair() {
        return linkFlair;
    }

    @NonNull
    public String getLinkTitle() {
        return linkTitle;
    }

    /**
     * Returns the url of the link and the empty string if there is no link url e.g. for a comment.
     */
    @NonNull
    public String getLinkUrl() {
        return linkUrl;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    @NonNull
    public String getPermalink() {
        return permalink;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    @NonNull
    public PostHint getPostHint() {
        return postHint;
    }

    public void setPostHint(@NonNull final PostHint postHint) {
        this.postHint = postHint;
    }

    @NonNull
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

    @NonNull
    public String getSubreddit() {
        return subreddit;
    }

    public void setLinkUrl(@NonNull final String linkUrl) {
        this.linkUrl = linkUrl;
    }

    @NonNull
    public CharSequence getDisplayAge() {
        return DateUtils.getRelativeTimeSpanString(
                TimeUnit.SECONDS.toMillis(createdUtc), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_NOON
                        | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_NO_MONTH_DAY);
    }

    @NonNull
    public Maybe<Spannable> getDisplayBody(@NonNull final Context context) {
        if (bodyHtml.isEmpty()) {
            return Maybe.empty();
        }
        final Spannable html = new SpannableString(trimTrailingWhitespace(Html.fromHtml(StringEscapeUtils.unescapeHtml4(bodyHtml))));
        Linkifier.addLinks(context, html);
        return Maybe.just(html);
    }

    @NonNull
    private static CharSequence trimTrailingWhitespace(@NonNull final CharSequence source) {
        int i = source.length();

        // loop back to the first non-whitespace character
        do {
            i--;
        } while (i > 0 && Character.isWhitespace(source.charAt(i)));

        return source.subSequence(0, i + 1);
    }

    public boolean isDescendantsVisible() {
        return descendantsVisible;
    }

    public void setDescendantsVisible(final boolean descendantsVisible) {
        this.descendantsVisible = descendantsVisible;
    }

    @NonNull
    public String getDisplayDescendants() {
        final int descendantCount = getDescendantCount().blockingGet();
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

    @NonNull
    public String getDisplayPoints(@NonNull final Context context, @NonNull final String scoreHiddenText) {
        if (scoreHidden) {
            return scoreHiddenText;
        } else {
            final Resources resources = context.getResources();
            return resources.getQuantityString(R.plurals.points, points, points);
        }
    }

    @NonNull
    @Override
    public Maybe<Integer> getDegree() {
        return Maybe.empty();
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> getChildren() {
        return children;
    }

    @NonNull
    @Override
    public Observable<Boolean> getTrigger() {
        return Observable.empty();
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> asObservableImpl() {
        return Observable.empty();
    }
}
