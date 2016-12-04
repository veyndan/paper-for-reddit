package com.veyndan.paper.reddit.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.binaryfork.spanny.Spanny;
import com.google.common.base.Splitter;
import com.veyndan.paper.reddit.MainActivity;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.post.Flair;
import com.veyndan.paper.reddit.util.Linkifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import io.reactivex.Maybe;

public class PostHeaderView extends TextView {

    @BindDimen(R.dimen.post_title_subtitle_spacing) int titleSubtitleSpacing;
    @BindDimen(R.dimen.post_subtitle_flair_spacing) int subtitleFlairSpacing;

    private final Context context;

    private final TextAppearanceSpan titleTextAppearanceSpan;

    private final TextAppearanceSpan subtitleTextAppearanceSpan;

    private final TextAppearanceSpan flairTextAppearanceSpan;
    private final StyleSpan flairStyleSpan = new StyleSpan(Typeface.BOLD);

    public PostHeaderView(final Context context) {
        this(context, null);
    }

    public PostHeaderView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PostHeaderView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ButterKnife.bind(this);

        this.context = context;

        titleTextAppearanceSpan = new TextAppearanceSpan(context, R.style.PostTitleTextAppearance);
        subtitleTextAppearanceSpan = new TextAppearanceSpan(context, R.style.PostSubtitleTextAppearance);
        flairTextAppearanceSpan = new TextAppearanceSpan(context, R.style.PostFlairTextAppearance);

        if (isInEditMode()) {
            final String text = getText().toString();

            final int flairStickiedColor = ContextCompat.getColor(context, R.color.post_flair_stickied);
            final int flairNsfwColor = ContextCompat.getColor(context, R.color.post_flair_nsfw);
            final int flairLinkColor = ContextCompat.getColor(context, R.color.post_flair_link);
            final int flairGildedColor = ContextCompat.getColor(context, R.color.post_flair_gilded);

            final Drawable flairGildedIcon = context.getDrawable(R.drawable.ic_star_white_12sp);

            final String flairStickiedText = context.getString(R.string.post_stickied);
            final String flairNsfwText = context.getString(R.string.post_nsfw);

            // Using regex (Pattern and Matcher) doesn't seem to work in edit mode.
            final Map<String, String> properties = Splitter.on(',').withKeyValueSeparator(':').split(text);

            final Maybe<String> title = properties.get("title") == null ? Maybe.empty() : Maybe.just(properties.get("title"));
            final Maybe<String> author = properties.get("author") == null ? Maybe.empty() : Maybe.just(properties.get("author"));
            final Maybe<CharSequence> age = properties.get("age") == null ? Maybe.empty() : Maybe.just(properties.get("age"));
            final Maybe<String> subreddit = properties.get("subreddit") == null ? Maybe.empty() : Maybe.just(properties.get("subreddit"));
            final List<Flair> flairs = new ArrayList<>(4);

            if (Boolean.valueOf(properties.getOrDefault("stickied", "false"))) {
                flairs.add(new Flair.Builder(flairStickiedColor)
                        .text(flairStickiedText)
                        .build());
            }

            if (Boolean.valueOf(properties.getOrDefault("nsfw", "false"))) {
                flairs.add(new Flair.Builder(flairNsfwColor)
                        .text(flairNsfwText)
                        .build());
            }

            if (properties.containsKey("linkFlair")) {
                flairs.add(new Flair.Builder(flairLinkColor)
                        .text(properties.get("linkFlair"))
                        .build());
            }

            if (properties.containsKey("gildedCount")) {
                flairs.add(new Flair.Builder(flairGildedColor)
                        .text(properties.get("gildedCount"))
                        .icon(flairGildedIcon)
                        .build());
            }

            if (title.isEmpty().blockingGet() || author.isEmpty().blockingGet() || age.isEmpty().blockingGet() || subreddit.isEmpty().blockingGet()) {
                throw new IllegalStateException("title, author, age, and subreddit must be set");
            }

            setHeader(title, author.blockingGet(), age.blockingGet(), subreddit.blockingGet(), flairs);
        }
    }

    public void setHeader(final Maybe<String> title, final String author, final CharSequence age,
                          final String subreddit, @NonNull final List<Flair> flairs) {
        final LineHeightSpan subtitleLineHeightSpan = new LineHeightSpan.WithDensity() {
            @Override
            public void chooseHeight(final CharSequence text, final int start, final int end,
                                     final int spanstartv, final int v,
                                     final Paint.FontMetricsInt fm, final TextPaint paint) {
                fm.ascent -= titleSubtitleSpacing;
                fm.top -= titleSubtitleSpacing;

                if (!flairs.isEmpty()) {
                    fm.descent += subtitleFlairSpacing;
                    fm.bottom += subtitleFlairSpacing;
                }
            }

            @Override
            public void chooseHeight(final CharSequence text, final int start, final int end,
                                     final int spanstartv, final int v,
                                     final Paint.FontMetricsInt fm) {
                chooseHeight(text, start, end, spanstartv, v, fm, null);
            }
        };

        setMovementMethod(LinkMovementMethod.getInstance());

        final ClickableSpan authorClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View widget) {
                final Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(Reddit.Filter.NODE_DEPTH, 0);
                intent.putExtra(Reddit.Filter.USER_NAME, author);
                intent.putExtra(Reddit.Filter.USER_COMMENTS, true);
                intent.putExtra(Reddit.Filter.USER_SUBMITTED, true);
                context.startActivity(intent);
            }
        };

        final ClickableSpan subredditClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View widget) {
                final Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(Reddit.Filter.NODE_DEPTH, 0);
                intent.putExtra(Reddit.Filter.SUBREDDIT_NAME, subreddit);
                context.startActivity(intent);
            }
        };

        final String delimiter = " · ";
        // Appending space to end of subtitle as no span is associated with it. This fixes a bug
        // where the subreddit clickable span spans the rest of the line, instead of being confined
        // to it's textual boundaries. TODO Figure out why this happens and fix it.
        final Spanny subtitle = new Spanny()
                .append(author, authorClickableSpan)
                .append(delimiter)
                .append(age)
                .append(delimiter)
                .append(subreddit, subredditClickableSpan)
                .append(" ");

        final Spanny spanny = new Spanny();

        if (title.count().blockingGet() == 1L) {
            spanny.append(title.blockingGet(), titleTextAppearanceSpan)
                    .append("\n");

            Linkifier.addLinks(context, spanny);
        }

        spanny.append(subtitle, subtitleTextAppearanceSpan, subtitleLineHeightSpan);

        if (!flairs.isEmpty()) {
            spanny.append("\n");

            final Spanny flairsSpanny = new Spanny();

            String divider = "";
            for (final Flair flair : flairs) {
                flairsSpanny.append(divider);
                if (divider.isEmpty()) {
                    divider = "   "; // TODO Replace with margin left and right of 4dp
                }

                flairsSpanny.append(createFlairSpannable(flair));
            }

            spanny.append(flairsSpanny, new LineHeightSpan.WithDensity() {
                @Override
                public void chooseHeight(final CharSequence text, final int start, final int end,
                                         final int spanstartv, final int v,
                                         final Paint.FontMetricsInt fm, final TextPaint paint) {
                    // Reset titleSubtitleSpacing.
                    fm.ascent += titleSubtitleSpacing;
                    fm.top += titleSubtitleSpacing;

                    // Reset subtitleFlairSpacing.
                    fm.descent -= subtitleFlairSpacing;
                    fm.bottom -= subtitleFlairSpacing;
                }

                @Override
                public void chooseHeight(final CharSequence text, final int start, final int end,
                                         final int spanstartv, final int v,
                                         final Paint.FontMetricsInt fm) {
                    chooseHeight(text, start, end, spanstartv, v, fm, null);
                }
            });
        }

        setText(spanny);
    }

    private SpannableString createFlairSpannable(final Flair flair) {
        final FlairBackgroundSpan flairBackgroundSpan = new FlairBackgroundSpan(context,
                flair.getBackgroundColor(), flair.getIcon());

        final String tag = flair.getText().blockingGet("");

        return Spanny.spanText(tag, flairTextAppearanceSpan, flairStyleSpan, flairBackgroundSpan);
    }

    private static final class FlairBackgroundSpan extends ReplacementSpan {

        @ColorInt private final int backgroundColor;
        @ColorInt private final int textColor;
        private final Maybe<Drawable> icon;

        private final int cornerRadius;
        private final int paddingHorizontal;
        private final int paddingVertical;
        private final int paddingDrawable;

        private FlairBackgroundSpan(final Context context, @ColorInt final int backgroundColor,
                                    final Maybe<Drawable> icon) {
            this.backgroundColor = backgroundColor;
            textColor = ContextCompat.getColor(context, android.R.color.white);
            this.icon = icon;

            final Resources resources = context.getResources();
            cornerRadius = resources.getDimensionPixelSize(R.dimen.post_flair_corner_radius);
            paddingHorizontal = resources.getDimensionPixelSize(R.dimen.post_flair_padding_horizontal);
            paddingVertical = resources.getDimensionPixelSize(R.dimen.post_flair_padding_vertical);
            paddingDrawable = resources.getDimensionPixelSize(R.dimen.post_flair_padding_drawable);
        }

        @Override
        public void draw(@NonNull final Canvas canvas, final CharSequence text, final int start,
                         final int end, final float x, final int top, final int y, final int bottom,
                         @NonNull final Paint paint) {
            final int drawablePadding = icon.isEmpty().blockingGet()
                    ? 0
                    : icon.blockingGet().getIntrinsicWidth() + paddingDrawable;

            final RectF rect = new RectF(x, top, x + paint.measureText(text, start, end) + paddingHorizontal * 2 + drawablePadding, bottom);
            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

            final TextPaint textPaint = new TextPaint(paint);
            textPaint.setColor(textColor);
            textPaint.setTextAlign(Paint.Align.CENTER);

            final float textHeight = textPaint.descent() - textPaint.ascent();
            final float textOffset = (textHeight / 2) - textPaint.descent();

            if (icon.count().blockingGet() == 1L) {
                final Rect paddedRect = new Rect(
                        (int) rect.left + paddingHorizontal,
                        (int) rect.top + paddingVertical,
                        (int) rect.right - paddingHorizontal,
                        (int) rect.bottom - paddingVertical);

                icon.blockingGet().setBounds(
                        paddedRect.left,
                        (int) (paddedRect.top + textPaint.descent()),
                        paddedRect.left + icon.blockingGet().getIntrinsicWidth(),
                        (int) (paddedRect.top + textPaint.descent() + icon.blockingGet().getIntrinsicHeight()));

                icon.blockingGet().draw(canvas);
            }

            canvas.drawText(text, start, end, rect.centerX() + drawablePadding / 2,
                    rect.centerY() + textOffset, textPaint);
        }

        @Override
        public int getSize(@NonNull final Paint paint, final CharSequence text, final int start,
                           final int end, final Paint.FontMetricsInt fm) {
            if (fm != null) {
                fm.descent += paddingVertical * 2;
                fm.bottom += paddingVertical * 2;
            }
            return Math.round(paint.measureText(text, start, end) + paddingHorizontal * 2);
        }
    }
}
