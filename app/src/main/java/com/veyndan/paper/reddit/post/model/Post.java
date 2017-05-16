package com.veyndan.paper.reddit.post.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.CheckResult;
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
import com.veyndan.paper.reddit.post.Flair;
import com.veyndan.paper.reddit.post.media.mutator.Mutators;
import com.veyndan.paper.reddit.util.Linkifier;
import com.veyndan.paper.reddit.util.MutableObject;
import com.veyndan.paper.reddit.util.Node;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collection;
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

        return builder()
                .comment(comment)
                .children(children)
                .commentCount(submission.getNumComments())
                .descendantsVisible(new MutableBoolean(comment))
                .archived(submission.archived)
                .author(submission.author == null ? "" : submission.author)
                .bodyHtml(submission.getBodyHtml())
                .createdUtc(submission.getCreatedUtc())
                .domain(submission.getDomain())
                .fullname(submission.getFullname())
                .gildedCount(submission.gilded)
                .hideable(submission.isHideable())
                .likes(new MutableObject<>(submission.getLikes()))
                .linkFlair(submission.getLinkFlairText())
                .linkTitle(submission.getLinkTitle())
                .linkUrl(submission.getLinkUrl() == null ? "" : submission.getLinkUrl())
                .locked(submission.isLocked())
                .nsfw(submission.isOver18())
                .permalink(submission.getPermalink())
                .points(new MutableInt(submission.score))
                .postHint(submission.getPostHint())
                .preview(submission.getPreview())
                .saved(new MutableBoolean(submission.saved))
                .scoreHidden(submission.isScoreHidden())
                .stickied(submission.stickied)
                .subreddit(submission.subreddit)
                .build();
    }

    static Builder builder() {
        return new AutoValue_Post.Builder()
                .medias(new MutableObject<>(Observable.empty()));
    }

    abstract Builder toBuilder();

    @CheckResult
    public Post withMedias(final Observable<Object> medias) {
        return toBuilder().medias(new MutableObject<>(medias)).build();
    }

    @CheckResult
    public Post withLinkUrl(final String linkUrl) {
        return toBuilder().linkUrl(linkUrl).build();
    }

    @CheckResult
    public Post withPostHint(final PostHint postHint) {
        return toBuilder().postHint(postHint).build();
    }

    public abstract MutableObject<Observable<Object>> medias();

    public abstract boolean comment();

    @NonNull
    @Override
    public abstract Observable<Node<Response<Thing<Listing>>>> children();

    @NonNull
    @Override
    public Single<Integer> descendantCount() {
        final Integer commentCount = commentCount();
        return commentCount == null ? super.descendantCount() : Single.just(commentCount);
    }

    @Nullable
    public abstract Integer commentCount();

    public abstract MutableBoolean descendantsVisible();

    public abstract boolean archived();

    public abstract String author();

    @Nullable
    public abstract String bodyHtml();

    public abstract long createdUtc();

    @Nullable
    public abstract String domain();

    public abstract String fullname();

    public abstract int gildedCount();

    public abstract boolean hideable();

    public abstract MutableObject<VoteDirection> likes();

    @Nullable
    public abstract String linkFlair();

    @Nullable
    public abstract String linkTitle();

    /**
     * Returns the url of the link and the empty string if there is no link url e.g. for a comment.
     */
    public abstract String linkUrl();

    public abstract boolean locked();

    public abstract boolean nsfw();

    public abstract String permalink();

    public abstract MutableInt points();

    public abstract PostHint postHint();

    public abstract Preview preview();

    public abstract MutableBoolean saved();

    public abstract boolean scoreHidden();

    public abstract boolean stickied();

    public abstract String subreddit();

    public final String article() {
        return fullname().substring(3, fullname().length());
    }

    public final boolean isGilded() {
        return gildedCount() > 0;
    }

    public final boolean hasLinkFlair() {
        return !TextUtils.isEmpty(linkFlair());
    }

    public final CharSequence getDisplayAge() {
        return DateUtils.getRelativeTimeSpanString(
                TimeUnit.SECONDS.toMillis(createdUtc()), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_NOON
                        | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_NO_MONTH_DAY);
    }

    @Nullable
    public final Spannable getDisplayBody(final Context context) {
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

    public final Single<String> getDisplayDescendants() {
        return descendantCount()
                .map(Post::quantityString);
    }

    public final String getDisplayPoints(final Context context) {
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

    public final Collection<Flair> flairs(final Context context) {
        final Collection<Flair> flairs = new ArrayList<>();

        if (stickied()) {
            flairs.add(Flair.stickied(context));
        }

        if (locked()) {
            flairs.add(Flair.locked(context));
        }

        if (nsfw()) {
            flairs.add(Flair.nsfw(context));
        }

        if (hasLinkFlair()) {
            flairs.add(Flair.link(context, linkFlair()));
        }

        if (isGilded()) {
            flairs.add(Flair.gilded(context, gildedCount()));
        }

        return flairs;
    }

    @Nullable
    @Override
    public final Integer degree() {
        return null;
    }

    @AutoValue.Builder
    interface Builder {

        Builder medias(MutableObject<Observable<Object>> medias);

        Builder comment(boolean comment);

        Builder children(Observable<Node<Response<Thing<Listing>>>> children);

        Builder commentCount(@Nullable Integer commentCount);

        Builder descendantsVisible(MutableBoolean descendantsVisible);

        Builder archived(boolean archived);

        Builder author(String author);

        Builder bodyHtml(@Nullable String bodyHtml);

        Builder createdUtc(long createdUtc);

        Builder domain(@Nullable String domain);

        Builder fullname(String fullname);

        Builder gildedCount(int gildedCount);

        Builder hideable(boolean hideable);

        Builder likes(MutableObject<VoteDirection> likes);

        Builder linkFlair(@Nullable String linkFlair);

        Builder linkTitle(@Nullable String linkTitle);

        Builder linkUrl(String linkUrl);

        Builder locked(boolean locked);

        Builder nsfw(boolean nsfw);

        Builder permalink(String permalink);

        Builder points(MutableInt points);

        Builder postHint(PostHint postHint);

        Builder preview(Preview preview);

        Builder saved(MutableBoolean saved);

        Builder scoreHidden(boolean scoreHidden);

        Builder stickied(boolean stickied);

        Builder subreddit(String subreddit);

        Post build();
    }
}
