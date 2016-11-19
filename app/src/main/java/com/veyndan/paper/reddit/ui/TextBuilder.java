package com.veyndan.paper.reddit.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Px;
import android.support.annotation.StyleRes;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.LineHeightSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.SparseArray;
import android.view.View;

import timber.log.Timber;

public final class TextBuilder {

    private static final int START = 0;
    private static final int FLAGS = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

    private static final SparseArray<StyleSpan> STYLE_RES_TO_SPAN = new SparseArray<>();

    static {
        STYLE_RES_TO_SPAN.append(Typeface.BOLD, new StyleSpan(Typeface.BOLD));
    }

    private final Context context;
    private final SpannableStringBuilder spannable;
    private final int end;

    public TextBuilder(final Context context, final CharSequence text) {
        this.context = context;
        spannable = new SpannableStringBuilder(text);
        end = text.length();
    }

    public TextBuilder onClick(final View.OnClickListener listener) {
        final ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View view) {
                listener.onClick(view);
            }
        };
        spannable.setSpan(clickableSpan, START, end, FLAGS);
        return this;
    }

    public TextBuilder padding(@Px final int left, @Px final int top,
                               @Px final int right, @Px final int bottom) {
        // TODO Note about text1 not being text and end1 not being end.
        final LineHeightSpan textLineHeightSpan = (text1, start, end1, spanstartv, v, fm) -> {
            final String[] subsections = TextUtils.split(text1.toString(), "\n");

            final String subsection = end1 <= subsections[0].length() + 1 ? subsections[0] : subsections[1];

            final int startSubsection = subsection.equals(subsections[0]) ? start : start - subsections[0].length();
            final int endSubsection = subsection.equals(subsections[0]) ? end1 : end1 - subsections[0].length();

            Timber.d("start=%3d end=%3d startSubsection=%3d endSubsection=%3d", start, end1, startSubsection, endSubsection);

            final String a = subsection.substring(startSubsection, Math.min(subsection.length(), endSubsection));
            final boolean b = subsection.endsWith(a);

            Timber.d(a);
            Timber.d(String.valueOf(b));

            if (b) {
                fm.bottom += bottom;
                fm.descent += bottom;
            }
        };

        spannable.setSpan(textLineHeightSpan, START, end, FLAGS);
        return this;
    }

    public TextBuilder textAppearance(@StyleRes final int textAppearance) {
        final TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(context, textAppearance);
        spannable.setSpan(textAppearanceSpan, START, end, FLAGS);
        return this;
    }

    public TextBuilder textStyle(final int style) {
        final StyleSpan styleSpan = STYLE_RES_TO_SPAN.get(style);

        if (styleSpan == null) {
            throw new IllegalStateException("Unknown style: " + style
                    + ". Must be from android.graphics.Typeface.");
        }

        spannable.setSpan(styleSpan, START, end, FLAGS);

        return this;
    }

    public CharSequence build() {
        return spannable;
    }
}
