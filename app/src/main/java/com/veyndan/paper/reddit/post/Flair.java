package com.veyndan.paper.reddit.post;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

import io.reactivex.Maybe;

public final class Flair {

    private final Maybe<String> text;
    private final Maybe<Drawable> icon;
    private final int backgroundColor;

    private Flair(final Builder builder) {
        text = builder.text;
        icon = builder.icon;
        backgroundColor = builder.backgroundColor;
    }

    public Maybe<String> getText() {
        return text;
    }

    public Maybe<Drawable> getIcon() {
        return icon;
    }

    @ColorInt
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public static class Builder {

        private Maybe<String> text = Maybe.empty();
        private Maybe<Drawable> icon = Maybe.empty();
        @ColorInt private final int backgroundColor;

        public Builder(@ColorInt final int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public Builder text(final String text) {
            this.text = Maybe.just(text);
            return this;
        }

        public Builder icon(final Drawable icon) {
            this.icon = Maybe.just(icon);
            return this;
        }

        public Flair build() {
            return new Flair(this);
        }
    }
}
