package com.veyndan.paper.reddit.image;

import android.content.Context;
import android.graphics.Bitmap;

import com.veyndan.paper.reddit.image.imp.CustomCache;
import com.veyndan.paper.reddit.util.Maybes;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public final class ImageLoader {

    public static Single<Bitmap> load(final String url, final Context context,
                                      final Single<Bitmap> network) {
        final Cache cache = CustomCache.getInstance(context);
        final Maybe<Bitmap> memory = Maybes.ofNullable(cache.get(url));

        final Maybe<Bitmap> disk = Maybe.empty();

        network
                .observeOn(AndroidSchedulers.mainThread()) // Ensures writing to cache from same thread.
                .doOnSuccess(bitmap -> cache.set(url, bitmap));

        return Observable.concat(memory.toObservable(), disk.toObservable(), network.toObservable())
                .firstOrError();
    }
}
