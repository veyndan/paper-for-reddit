package com.veyndan.paper.reddit.image;

import android.content.Context;

import com.veyndan.paper.reddit.image.imp.CustomCache;
import com.veyndan.paper.reddit.util.Maybes;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public final class ImageLoader {

    public static <T> Single<T> load(final String key, final Context context,
                                     final Single<T> network) {
        final Cache<T> cache = CustomCache.getInstance(context);
        final Maybe<T> memory = Maybes.ofNullable(cache.get(key));

        final Maybe<T> disk = Maybe.empty();

        network
                .observeOn(AndroidSchedulers.mainThread()) // Ensures writing to cache from same thread.
                .doOnSuccess(value -> cache.set(key, value));

        return Observable.concat(memory.toObservable(), disk.toObservable(), network.toObservable())
                .firstOrError();
    }
}
