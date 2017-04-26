package com.veyndan.paper.reddit.post;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.auto.value.AutoValue;
import com.veyndan.paper.reddit.R;

import static com.google.common.base.Preconditions.checkState;

@AutoValue
public abstract class Flair {

    public static Flair stickied(final Context context) {
        return builder()
                .backgroundColor(ContextCompat.getColor(context, R.color.post_flair_stickied))
                .text(context.getString(R.string.post_stickied))
                .build();
    }

    public static Flair locked(final Context context) {
        return builder()
                .backgroundColor(ContextCompat.getColor(context, R.color.post_flair_locked))
                .text(context.getString(R.string.post_locked))
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_lock_outline_white_12sp))
                .build();
    }

    public static Flair nsfw(final Context context) {
        return builder()
                .backgroundColor(ContextCompat.getColor(context, R.color.post_flair_nsfw))
                .text(context.getString(R.string.post_nsfw))
                .searchQuery("nsfw:yes")
                .build();
    }

    public static Flair link(final Context context, final String text) {
        return builder()
                .backgroundColor(ContextCompat.getColor(context, R.color.post_flair_link))
                .text(text)
                .searchQuery(String.format("flair:'%s'", text))
                .build();
    }

    public static Flair gilded(final Context context, final int gildedCount) {
        return builder()
                .backgroundColor(ContextCompat.getColor(context, R.color.post_flair_gilded))
                .text(String.valueOf(gildedCount))
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_star_white_12sp))
                .build();
    }

    public abstract int backgroundColor();

    @Nullable
    public abstract String text();

    @Nullable
    public abstract Drawable icon();

    @Nullable
    public abstract String searchQuery();

    public final boolean searchable() {
        return searchQuery() != null;
    }

    public static Builder builder() {
        return new AutoValue_Flair.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {

        abstract Builder backgroundColor(@ColorInt int backgroundColor);

        abstract Builder text(String text);

        abstract Builder icon(Drawable icon);

        abstract Builder searchQuery(String searchQuery);

        abstract Flair autoBuild();

        final Flair build() {
            final Flair flair = autoBuild();
            checkState(flair.backgroundColor() != 0, "backgroundColor must be set");
            return flair;
        }
    }
}
