package com.veyndan.paper.reddit;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RedditProvider extends ContentProvider {

    public static final String ROOT_AUTHORITY = "com.veyndan.paper.reddit";

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull final Uri uri, final String[] strings, final String s,
                        final String[] strings1, final String s1) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public String getType(@NonNull final Uri uri) {
        return "vnd.android.cursor.item/vnd." + ROOT_AUTHORITY;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull final Uri uri, final ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull final Uri uri, final String s, final String[] strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull final Uri uri, final ContentValues contentValues, final String s,
                      final String[] strings) {
        throw new UnsupportedOperationException();
    }
}
