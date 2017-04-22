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
    public interface Builder {

        Builder type(Type type);

        Builder text(String text);

        Builder icon(Drawable icon);

        Builder backgroundColor(@ColorInt int backgroundColor);

        Flair build();
    }
}
