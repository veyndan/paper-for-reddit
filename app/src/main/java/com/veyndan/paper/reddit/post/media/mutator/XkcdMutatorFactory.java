package com.veyndan.paper.reddit.post.media.mutator;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.api.xkcd.network.XkcdService;
import com.veyndan.paper.reddit.post.media.model.Image;
import com.veyndan.paper.reddit.post.model.Post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Maybe;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

final class XkcdMutatorFactory implements MutatorFactory {

    private static final Pattern PATTERN = Pattern.compile("^https?://(?:www\\.)?xkcd\\.com/(\\d+)/?$");

    static XkcdMutatorFactory create() {
        return new XkcdMutatorFactory();
    }

    private XkcdMutatorFactory() {
    }

    @Override
    public Maybe<Post> mutate(final Post post) {
        final Matcher matcher = PATTERN.matcher(post.linkUrl());

        return Single.just(post)
                .filter(post1 -> matcher.matches())
                .map(post1 -> {
                    final int comicNum = Integer.parseInt(matcher.group(1));

                    final Retrofit retrofit = new Retrofit.Builder()
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(MoshiConverterFactory.create())
                            .baseUrl("https://xkcd.com")
                            .build();

                    final XkcdService xkcdService = retrofit.create(XkcdService.class);

                    final Single<Image> image = xkcdService.num(comicNum)
                            .map(comic -> Image.create(comic.body().getImg()));

                    return post1
                            .withPostHint(PostHint.IMAGE)
                            .withMedias(post1.medias().value.concatWith(image.toObservable()));
                });
    }
}
