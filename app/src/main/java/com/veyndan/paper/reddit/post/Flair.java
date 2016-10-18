package com.veyndan.paper.reddit.post;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

public final class Flair {

    @Nullable private final String text;
    @Nullable private final Drawable icon;
    private final int backgroundColor;

    private Flair(final Builder builder) {
        text = builder.text;
        icon = builder.icon;
        backgroundColor = builder.backgroundColor;
    }

    @Nullable
    public String getText() {
        return text;
    }

    @Nullable
    public Drawable getIcon() {
        return icon;
    }

    @ColorInt
    public int getBackgroundColor() {
        return backgroundColor;
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
}
