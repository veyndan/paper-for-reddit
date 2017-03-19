package com.veyndan.paper.reddit.image.imp;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.util.LruCache;

import com.veyndan.paper.reddit.image.Cache;

public class CustomCache<T> implements Cache<T> {

    private static CustomCache INSTANCE;

    private final LruCache<String, T> cache;

    public static <T> CustomCache<T> getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CustomCache(context);
        }
        return INSTANCE;
    }

    private CustomCache(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final int availableMemoryInBytes = activityManager.getMemoryClass() * 1024 * 1024;
        cache = new LruCache<String, T>(availableMemoryInBytes / 8) {
            @Override
            protected int sizeOf(final String key, final T value) {
                // TODO Use a Factory here to get this as getByteCount() is a method of Bitmap. Or enum?
                return value.getByteCount();
            }
        };
    }

    @Override
    public T get(final String key) {
        return cache.get(key);
    }

    @Override
    public void set(final String key, final T value) {
        cache.put(key, value);
    }
}
