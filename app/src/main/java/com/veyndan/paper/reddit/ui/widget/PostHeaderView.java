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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
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
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.veyndan.paper.reddit.Filter;
import com.veyndan.paper.reddit.MainActivity;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.post.Flair;
import com.veyndan.paper.reddit.ui.TextBuilder;
import com.veyndan.paper.reddit.util.Linkifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import timber.log.Timber;

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

            final String title = properties.get("title");
            final String author = properties.get("author");
            final CharSequence age = properties.get("age");
            final String subreddit = properties.get("subreddit");
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

            if (title == null || author == null || age == null || subreddit == null) {
                throw new IllegalStateException("title, author, age, and subreddit must be set");
            }

            setHeader(title, author, age, subreddit, flairs);
        }
    }

    public void setHeader(final String title, final String author, final CharSequence age,
                          final String subreddit, @NonNull final Collection<Flair> flairs) {
        final CharSequence titleSpannable = new TextBuilder(context, title + '\n')
                .padding(0, 0, 0, titleSubtitleSpacing)
                .textAppearance(R.style.PostTitleTextAppearance)
                .build();

        final CharSequence authorSpannable = new TextBuilder(context, author)
                .onClick(view -> {
                    final Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(Filter.USER_NAME, author);
                    intent.putExtra(Filter.USER_COMMENTS, true);
                    intent.putExtra(Filter.USER_SUBMITTED, true);
                    context.startActivity(intent);
                })
                .padding(0, 0, 0, flairs.isEmpty() ? 0 : subtitleFlairSpacing)
                .textAppearance(R.style.PostSubtitleTextAppearance)
                .build();

        final String subtitleDelimiter = " · ";

        final CharSequence ageSpannable = new TextBuilder(context, subtitleDelimiter + age + subtitleDelimiter)
                .textAppearance(R.style.PostSubtitleTextAppearance)
                .build();

        final CharSequence subredditSpannable = new TextBuilder(context, subreddit)
                .onClick(view -> {
                    final Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(Filter.SUBREDDIT_NAME, subreddit);
                    context.startActivity(intent);
                })
                .textAppearance(R.style.PostSubtitleTextAppearance)
                .build();

        final SpannableStringBuilder spannable = new SpannableStringBuilder()
                .append(titleSpannable)
                .append(authorSpannable)
                .append(ageSpannable)
                .append(subredditSpannable);

        if (!flairs.isEmpty()) {
            spannable.append("\n");
            for (final Flair flair : flairs) {
                final FlairBackgroundSpan flairBackgroundSpan = new FlairBackgroundSpan(context,
                        flair.getBackgroundColor(), flair.getIcon());

                final String tag = MoreObjects.firstNonNull(flair.getText(), "");

                final CharSequence flairSpannable = new TextBuilder(context, Spanny.spanText(tag, flairStyleSpan, flairBackgroundSpan))
                        .textAppearance(R.style.PostFlairTextAppearance)
                        .textStyle(Typeface.BOLD)
                        .build();

                spannable.append(flairSpannable);
            }
        }

        setText(spannable);
    }

    private static class PaddingSpan implements LineHeightSpan {

        private final int paddingBottom;

        PaddingSpan(final int paddingBottom) {
            this.paddingBottom = paddingBottom;
        }

        @Override
        public void chooseHeight(final CharSequence text, final int start, final int end,
                                 final int spanstartv, final int v, final Paint.FontMetricsInt fm) {
            final String[] subsections = TextUtils.split(text.toString(), "\n");

            final String subsection = end <= subsections[0].length() + 1 ? subsections[0] : subsections[1];

            final int startSubsection = subsection.equals(subsections[0]) ? start : start - subsections[0].length();
            final int endSubsection = subsection.equals(subsections[0]) ? end : end - subsections[0].length();

            Timber.d("start=%3d end=%3d startSubsection=%3d endSubsection=%3d", start, end, startSubsection, endSubsection);

            final String a = subsection.substring(startSubsection, Math.min(subsection.length(), endSubsection));
            final boolean b = subsection.endsWith(a);

            Timber.d(a);
            Timber.d(String.valueOf(b));

            if (b) {
                fm.bottom += paddingBottom;
                fm.descent += paddingBottom;
            }
        }
    }

    public void setHeader1(final String title, final String author, final CharSequence age,
                           final String subreddit, @NonNull final List<Flair> flairs) {
        final LineHeightSpan textLineHeightSpan = new LineHeightSpan.WithDensity() {
            @Override
            public void chooseHeight(final CharSequence text, final int start, final int end,
                                     final int spanstartv, final int v,
                                     final Paint.FontMetricsInt fm) {
                chooseHeight(text, start, end, spanstartv, v, fm, null);
            }

            @Override
            public void chooseHeight(final CharSequence text, final int start, final int end,
                                     final int spanstartv, final int v,
                                     final Paint.FontMetricsInt fm, final TextPaint paint) {

                Timber.d("text=%s start=%s end=%s spanstartv=%s v=%s", text, start, end, spanstartv, v);
                Timber.d("lineIndex=%s string=%s", end - start, text.subSequence(start, end));

                Timber.d(text.toString().substring(0, text.toString().indexOf('\n')));

                if (text.toString().substring(0, text.toString().indexOf('\n')).contains(text.subSequence(start, end).toString())) {
                    fm.bottom += 100;
                    fm.descent += 100;
                }
            }
        };

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
                intent.putExtra(Filter.USER_NAME, author);
                intent.putExtra(Filter.USER_COMMENTS, true);
                intent.putExtra(Filter.USER_SUBMITTED, true);
                context.startActivity(intent);
            }
        };

        final ClickableSpan subredditClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View widget) {
                final Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(Filter.SUBREDDIT_NAME, subreddit);
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

        if (title != null) {
            spanny.append(title, titleTextAppearanceSpan, new PaddingSpan(titleSubtitleSpacing))
                    .append("\n");

            Linkifier.addLinks(context, spanny);
        }

        spanny.append(subtitle, subtitleTextAppearanceSpan, new PaddingSpan(flairs.isEmpty() ? 0 : subtitleFlairSpacing));

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

        final String tag = MoreObjects.firstNonNull(flair.getText(), "");

        return Spanny.spanText(tag, flairTextAppearanceSpan, flairStyleSpan, flairBackgroundSpan);
    }

    private static final class FlairBackgroundSpan extends ReplacementSpan {

        @ColorInt private final int backgroundColor;
        @ColorInt private final int textColor;
        @Nullable private final Drawable icon;

        private final int cornerRadius;
        private final int paddingHorizontal;
        private final int paddingVertical;
        private final int paddingDrawable;

        private FlairBackgroundSpan(final Context context, @ColorInt final int backgroundColor,
                                    @Nullable final Drawable icon) {
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
            final int drawablePadding = icon == null
                    ? 0
                    : icon.getIntrinsicWidth() + paddingDrawable;

            final RectF rect = new RectF(x, top, x + paint.measureText(text, start, end) + paddingHorizontal * 2 + drawablePadding, bottom);
            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

            final TextPaint textPaint = new TextPaint(paint);
            textPaint.setColor(textColor);
            textPaint.setTextAlign(Paint.Align.CENTER);

            final float textHeight = textPaint.descent() - textPaint.ascent();
            final float textOffset = (textHeight / 2) - textPaint.descent();

            if (icon != null) {
                final Rect paddedRect = new Rect(
                        (int) rect.left + paddingHorizontal,
                        (int) rect.top + paddingVertical,
                        (int) rect.right - paddingHorizontal,
                        (int) rect.bottom - paddingVertical);

                icon.setBounds(
                        paddedRect.left,
                        (int) (paddedRect.top + textPaint.descent()),
                        paddedRect.left + icon.getIntrinsicWidth(),
                        (int) (paddedRect.top + textPaint.descent() + icon.getIntrinsicHeight()));

                icon.draw(canvas);
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
