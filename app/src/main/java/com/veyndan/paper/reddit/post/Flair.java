package com.veyndan.paper.reddit.post;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Flair {

    public enum Type {
        LINK, UNDEFINED
    }

    @NonNull
    public abstract Type type();

    @Nullable
    public abstract String text();

    @Nullable
    public abstract Drawable icon();

    public abstract int backgroundColor();

    public static Builder builder(@ColorInt final int backgroundColor) {
        return new AutoValue_Flair.Builder()
                .backgroundColor(backgroundColor)
                .type(Type.UNDEFINED);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder type(Type type);

        public abstract Builder text(String text);

        public abstract Builder icon(Drawable icon);

        public abstract Builder backgroundColor(@ColorInt int backgroundColor);

        public abstract Flair build();
    }
}
