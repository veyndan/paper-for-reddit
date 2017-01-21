package com.veyndan.paper.reddit.image;

import android.graphics.Bitmap;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Single;

public class ImageLoader {

    private final Cache cache;
    private final Network network;
    private final Decoder decoder;

    public ImageLoader(final Cache cache, final Network network, final Decoder decoder) {
        this.cache = cache;
        this.network = network;
        this.decoder = decoder;
    }

    public Single<Bitmap> load(final String url) {
        return Maybe.concat(memory(cache, url), network(url, cache, network, decoder).toMaybe())
                .firstOrError();
    }

    private static MaybeSource<Bitmap> memory(final Cache cache, final String url) {
        final Bitmap bitmap = cache.get(url);
        return bitmap == null ? Maybe.empty() : Maybe.just(bitmap);
    }

    private static Single<Bitmap> network(final String url,
                                          final Cache cache, final Network network,
                                          final Decoder decoder) {
        return Single.just(url)
                .flatMap(network::getImageAsInputStream)
                .map(decoder::decodeInputStream)
                .doOnSuccess(bitmap -> cache.set(url, bitmap));
    }
}
