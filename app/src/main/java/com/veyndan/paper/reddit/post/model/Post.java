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
import android.util.MutableBoolean;
import android.util.MutableInt;

import com.google.auto.value.AutoValue;
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
import com.veyndan.paper.reddit.util.MutableObject;
import com.veyndan.paper.reddit.util.Node;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Response;

@AutoValue
public abstract class Post extends Node<Response<Thing<Listing>>> {

    public static Post create(@NonNull final Submission submission) {
        final boolean comment = submission instanceof Comment;

        final Observable<Node<Response<Thing<Listing>>>> children = Observable.fromIterable(submission.getReplies().data.children)
                .flatMapSingle(redditObject -> {
                    if (redditObject instanceof Submission) {
                        return Single.just(redditObject)
                                .cast(Submission.class)
                                .map(Post::create)
                                .flatMap(Mutators.mutate());
                    } else if (redditObject instanceof More) {
                        return Single.just(redditObject)
                                .cast(More.class)
                                .map(more -> new Progress.Builder()
                                        .degree(more.count)
                                        .build());
                    } else {
                        final String message = "Unknown node class: " + redditObject;
                        return Single.error(new IllegalStateException(message));
                    }
                });

        final MutableBoolean descendantsVisible = new MutableBoolean(comment);

        final boolean archived = submission.archived;
        final String author = submission.author == null ? "" : submission.author;
        final String bodyHtml = submission.getBodyHtml();
        final long createdUtc = submission.getCreatedUtc();
        final String domain = submission.getDomain();
        final String fullname = submission.getFullname();
        final int gildedCount = submission.gilded;
        final boolean hideable = submission.isHideable();
        final MutableObject<VoteDirection> likes = new MutableObject<>(submission.getLikes());
        final String linkFlair = submission.getLinkFlairText();
        final String linkTitle = submission.getLinkTitle();
        final MutableObject<String> linkUrl = new MutableObject<>(
                submission.getLinkUrl() == null ? "" : submission.getLinkUrl());
        final boolean locked = submission.isLocked();
        final boolean nsfw = submission.isOver18();
        final String permalink = submission.getPermalink();
        final MutableInt points = new MutableInt(submission.score);

        final MutableObject<PostHint> postHint = new MutableObject<>(submission.getPostHint());

        final Preview preview = submission.getPreview();
        final MutableBoolean saved = new MutableBoolean(submission.saved);
        final boolean scoreHidden = submission.isScoreHidden();
        final boolean stickied = submission.stickied;
        final String subreddit = submission.subreddit;

        final Post post = new AutoValue_Post(new ArrayList<>(), comment, children,
                descendantsVisible, archived, author, bodyHtml, createdUtc, domain, fullname,
                gildedCount, hideable, likes, linkFlair, linkTitle, linkUrl, locked, nsfw,
                permalink, points, postHint, preview, saved, scoreHidden, stickied, subreddit);
        post.setDescendantCount(submission.getNumComments());
        return post;
    }

    public abstract List<Object> medias();

    public abstract boolean comment();

    public abstract Observable<Node<Response<Thing<Listing>>>> children();

    public abstract MutableBoolean descendantsVisible();

    public abstract boolean archived();

    public abstract String author();

    @Nullable
    public abstract String bodyHtml();

    public abstract long createdUtc();

    public abstract String domain();

    public abstract String fullname();

    public abstract int gildedCount();

    public abstract boolean hideable();

    public abstract MutableObject<VoteDirection> likes();

    @Nullable
    public abstract String linkFlair();

    public abstract String linkTitle();

    /**
     * Returns the url of the link and the empty string if there is no link url e.g. for a comment.
     */
    public abstract MutableObject<String> linkUrl();

    public abstract boolean locked();

    public abstract boolean nsfw();

    public abstract String permalink();

    public abstract MutableInt points();

    public abstract MutableObject<PostHint> postHint();

    public abstract Preview preview();

    public abstract MutableBoolean saved();

    public abstract boolean scoreHidden();

    public abstract boolean stickied();

    public abstract String subreddit();

    public final String article() {
        return fullname().substring(3, fullname().length());
    }

    public boolean isGilded() {
        return gildedCount() > 0;
    }

    public boolean hasLinkFlair() {
        return !TextUtils.isEmpty(linkFlair());
    }

    public CharSequence getDisplayAge() {
        return DateUtils.getRelativeTimeSpanString(
                TimeUnit.SECONDS.toMillis(createdUtc()), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_NOON
                        | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_NO_MONTH_DAY);
    }

    @Nullable
    public Spannable getDisplayBody(final Context context) {
        if (TextUtils.isEmpty(bodyHtml())) {
            return null;
        }
        final Spannable html = new SpannableString(trimTrailingWhitespace(Html.fromHtml(StringEscapeUtils.unescapeHtml4(bodyHtml()))));
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

    public String getDisplayDescendants() {
        return quantityString(getDescendantCount());
    }

    public String getDisplayPoints(final Context context) {
        final Resources resources = context.getResources();

        if (scoreHidden()) {
            return resources.getString(R.string.score_hidden);
        } else {
            final String formattedString = quantityString(points().value);
            return resources.getQuantityString(R.plurals.points, points().value, formattedString);
        }
    }

    private static String quantityString(final int num) {
        if (num < 1000) {
            return String.valueOf(num);
        } else {
            final int beforeDecimal = num / 1000;
            final int afterDecimal = num % 1000 / 100;

            final int maxStringSize = 5; // e.g. "99.9K"
            final StringBuilder result = new StringBuilder(maxStringSize);
            result.append(beforeDecimal);
            if (afterDecimal > 0) {
                result.append('.').append(afterDecimal);
            }
            result.append('K');
            return result.toString();
        }
    }

    @Nullable
    @Override
    public Integer getDegree() {
        return null;
    }

    @NonNull
    @Override
    public Observable<Node<Response<Thing<Listing>>>> getChildren() {
        return children();
    }
}
