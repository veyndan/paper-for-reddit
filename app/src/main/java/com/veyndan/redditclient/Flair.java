package com.veyndan.redditclient;

import android.content.Context;
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
import android.text.style.ReplacementSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;

import com.binaryfork.spanny.Spanny;
import com.google.common.base.Optional;

import timber.log.Timber;

public final class Flair {

    private final Optional<String> text;
    private final Optional<Drawable> icon;
    private final int backgroundColor;

    private Flair(final Builder builder) {
        this.text = Optional.fromNullable(builder.text);
        this.icon = Optional.fromNullable(builder.icon);
        this.backgroundColor = builder.backgroundColor;
    }

    public Optional<String> getText() {
        return text;
    }

    public Optional<Drawable> getIcon() {
        return icon;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public SpannableString getSpannable(final Context context) {
        final TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(context, R.style.PostFlairTextAppearance);
        final StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        final BackgroundSpan backgroundSpan = new BackgroundSpan(context, backgroundColor, icon);

        final StringBuilder tagBuilder = new StringBuilder(2);

        if (text.isPresent()) {
            tagBuilder.append(text.get());
        }

        return Spanny.spanText(tagBuilder, textAppearanceSpan, styleSpan, backgroundSpan);
    }

    public static class Builder {

        private String text;
        private Drawable icon;
        @ColorInt private final int backgroundColor;

        public Builder(@ColorInt final int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public Builder text(final String text) {
            this.text = text;
            return this;
        }

        public Builder icon(final Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Flair build() {
            return new Flair(this);
        }
    }

    public static class BackgroundSpan extends ReplacementSpan {

        @ColorInt private final int backgroundColor;
        @ColorInt private final int textColor;
        private final Optional<Drawable> icon;

        private final int cornerRadius;
        private final int paddingHorizontal;
        private final int paddingVertical;
        private final int paddingDrawable;

        public BackgroundSpan(final Context context, @ColorInt final int backgroundColor, final Optional<Drawable> icon) {
            this.backgroundColor = backgroundColor;
            textColor = ContextCompat.getColor(context, android.R.color.white);
            this.icon = icon;

            cornerRadius = context.getResources().getDimensionPixelSize(R.dimen.post_flair_corner_radius);
            paddingHorizontal = context.getResources().getDimensionPixelSize(R.dimen.post_flair_padding_horizontal);
            paddingVertical = context.getResources().getDimensionPixelSize(R.dimen.post_flair_padding_vertical);
            paddingDrawable = context.getResources().getDimensionPixelSize(R.dimen.post_flair_padding_drawable);
        }

        @Override
        public void draw(@NonNull final Canvas canvas, final CharSequence text, final int start, final int end, final float x, final int top, final int y, final int bottom, @NonNull final Paint paint) {
            int drawablePadding = 0;
            if (icon.isPresent()) {
                drawablePadding += icon.get().getIntrinsicWidth() + paddingDrawable;
            }

            final RectF rect = new RectF(x, top, x + paint.measureText(text, start, end) + paddingHorizontal * 2 + drawablePadding, bottom);
            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

            final TextPaint textPaint = new TextPaint(paint);
            textPaint.setColor(textColor);
            textPaint.setTextAlign(Paint.Align.CENTER);

            final float textHeight = textPaint.descent() - textPaint.ascent();
            final float textOffset = (textHeight / 2) - textPaint.descent();

            if (icon.isPresent()) {
                Timber.d("Subtext: %s\tx: %s\ty: %s\ttop: %s\tbottom: %s\ttextHeight: %s\ttextOffset: %s\tascent: %s\tdescent: %s", text.subSequence(start, end), x, y, top, bottom, textHeight, textOffset, textPaint.ascent(), textPaint.descent());
                final Rect paddedRect = new Rect((int) rect.left + paddingHorizontal, (int) rect.top + paddingVertical, (int) rect.right - paddingHorizontal, (int) rect.bottom - paddingVertical);
                icon.get().setBounds(paddedRect.left, (int) (paddedRect.top + textPaint.descent()), paddedRect.left + icon.get().getIntrinsicWidth(), (int) (paddedRect.top + textPaint.descent() + icon.get().getIntrinsicHeight()));
                icon.get().draw(canvas);
            }

            canvas.drawText(text, start, end, rect.centerX() + drawablePadding / 2, rect.centerY() + textOffset, textPaint);
        }

        @Override
        public int getSize(@NonNull final Paint paint, final CharSequence text, final int start, final int end, final Paint.FontMetricsInt fm) {
            if (fm != null) {
                fm.descent += paddingVertical * 2;
                fm.bottom += paddingVertical * 2;
            }
            return Math.round(paint.measureText(text, start, end) + paddingHorizontal * 2);
        }
    }
}
