package com.veyndan.paper.reddit.post.media.mutator;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.api.xkcd.network.XkcdService;
import com.veyndan.paper.reddit.post.model.Post;
import com.veyndan.paper.reddit.post.media.model.Image;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

final class XkcdMutatorFactory implements MutatorFactory {

    private static final Pattern PATTERN = Pattern.compile("^https?://(?:www\\.)?xkcd\\.com/(\\d+)/?$");

    static XkcdMutatorFactory create() {
        return new XkcdMutatorFactory();
    }

    private XkcdMutatorFactory() {
    }

    @Override
    public Observable<Post> mutate(final Post post) {
        final Matcher matcher = PATTERN.matcher(post.getLinkUrl());

        return Observable.just(post)
                .filter(post1 -> post1.isLink() && matcher.matches())
                .map(post1 -> {
                    final int comicNum = Integer.parseInt(matcher.group(1));

                    final Retrofit retrofit = new Retrofit.Builder()
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl("https://xkcd.com")
                            .build();

                    final XkcdService xkcdService = retrofit.create(XkcdService.class);

                    final Observable<Image> imageObservable = xkcdService.num(comicNum)
                            .map(comic -> new Image(comic.getImg()));

                    post.setPostHint(PostHint.IMAGE);

                    post1.setMediaObservable(imageObservable);
                    return post1;
                });
    }
}
