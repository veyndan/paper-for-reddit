package com.veyndan.paper.reddit.image.imp;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.veyndan.paper.reddit.image.Cache;

public class CustomCache implements Cache {

    private static CustomCache INSTANCE;

    private final LruCache<String, Bitmap> cache;

    public static CustomCache getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CustomCache(context);
        }
        return INSTANCE;
    }

    private CustomCache(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final int availableMemoryInBytes = activityManager.getMemoryClass() * 1024 * 1024;
        cache = new LruCache<String, Bitmap>(availableMemoryInBytes / 8) {
            @Override
            protected int sizeOf(final String key, final Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    public Bitmap get(final String key) {
        return cache.get(key);
    }

    @Override
    public void set(final String key, final Bitmap bitmap) {
        cache.put(key, bitmap);
    }
}
