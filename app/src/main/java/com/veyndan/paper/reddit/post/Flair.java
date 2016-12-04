package com.veyndan.paper.reddit.post;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import io.reactivex.Maybe;

public final class Flair {

    @NonNull private final Maybe<String> text;
    @NonNull private final Maybe<Drawable> icon;
    private final int backgroundColor;

    private Flair(@NonNull final Builder builder) {
        text = builder.text;
        icon = builder.icon;
        backgroundColor = builder.backgroundColor;
    }

    @NonNull
    public Maybe<String> getText() {
        return text;
    }

    @NonNull
    public Maybe<Drawable> getIcon() {
        return icon;
    }

    @ColorInt
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public static class Builder {

        @NonNull private Maybe<String> text = Maybe.empty();
        @NonNull private Maybe<Drawable> icon = Maybe.empty();
        @ColorInt private final int backgroundColor;

        public Builder(@ColorInt final int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        @NonNull
        public Builder text(@NonNull final String text) {
            this.text = Maybe.just(text);
            return this;
        }

        @NonNull
        public Builder icon(@NonNull final Drawable icon) {
            this.icon = Maybe.just(icon);
            return this;
        }

        @NonNull
        public Flair build() {
            return new Flair(this);
        }
    }
}
